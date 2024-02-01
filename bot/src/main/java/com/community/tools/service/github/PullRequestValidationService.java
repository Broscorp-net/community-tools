package com.community.tools.service.github;

import com.community.tools.dto.CommitFileDto;
import com.community.tools.dto.OpenAiValidationResponseDto;
import com.community.tools.dto.QueuedValidationProcessDto;
import com.community.tools.service.NotificationService;
import com.community.tools.service.openai.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestReviewBuilder;
import org.kohsuke.github.GHPullRequestReviewEvent;
import org.kohsuke.github.GHRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class PullRequestValidationService {
  private final GitHubConnectService gitService;
  private final ObjectMapper objectMapper;
  private final OpenAiService openAiService;
  private final NotificationService notificationService;
  private final Queue<QueuedValidationProcessDto> requests = new LinkedList<>();
  private static final String PROMPT_TEMPLATE = "Provide me with the code review where you "
          + "point out lines of code that have some issues and make an comment "
          + "what are they and why. Then rate me the code on the scale from 1 to 10 "
          + "where 1 is the poorest. Rate code considering it's trainee developer."
          + "The code will be provided in JSON format as "
          + "[{filename: ${filename}, code: ${code}}]. Ignore git patch headers. "
          + "The code to rate is: %s"
          + ". Return me response in the JSON format of {"
          + "  rating: ${rating_integer},"
          + "  files: ["
          + "    filename: ${filename},"
          + "    comments: ["
          + "      {"
          + "        line: ${line_number_integer},"
          + "        comment: ${comment}"
          + "      }"
          + "    ]"
          + "  ]"
          + "} return only strictly json response.";
  private static final String NOTIFICATION_MESSAGE_TEMPLATE
          = "The commit %s was reviewed. Please check it out.";

  /**
   * Constructor for the service.
   *
   * @param gitService          git service to get repositories
   * @param objectMapper        mapper to map responses to desired object
   * @param openAiService       ai service to validate PRs
   * @param notificationService service to notification users after pr validation
   */
  public PullRequestValidationService(GitHubConnectService gitService,
                                      ObjectMapper objectMapper, OpenAiService openAiService,
                                      NotificationService notificationService) {
    this.gitService = gitService;
    this.objectMapper = objectMapper;
    this.openAiService = openAiService;
    this.notificationService = notificationService;
  }

  /**
   * Main method of the service that retrieves Feedback PR by its id(1),
   * iterates through its commits(except those by bot created)
   * and gathers all commit files into one prompt to put it in the queue of validation.
   *
   * @param repositoryName name of the repository that needs its PR to be validated.
   * @throws RuntimeException If github api doesn't find requested objects
   */
  public void validatePullRequest(String repositoryName) {
    GHRepository repository = gitService.getGitHubRepositoryByName(repositoryName);
    try {
      GHPullRequest pullRequest = repository.getPullRequest(1);

      List<GHPullRequestCommitDetail> userCommits = pullRequest
              .listCommits().toList()
              .stream()
              .filter(commit ->
                      !commit.getCommit().getAuthor().getName().equals("github-classroom[bot]")
              )
              .collect(Collectors.toList());

      for (GHPullRequestCommitDetail commitDetail : userCommits) {
        List<CommitFileDto> fileList = new ArrayList<>();
        List<GHCommit.File> commitFiles = repository
                .getCommit(commitDetail.getSha()).getFiles()
                .stream().filter(Objects::nonNull).collect(Collectors.toList());

        for (GHCommit.File file : commitFiles) {
          fileList.add(new CommitFileDto(file.getFileName(), file.getPatch()));
        }
        String prompt = String.format(PROMPT_TEMPLATE, objectMapper.writeValueAsString(fileList));

        requests.add(QueuedValidationProcessDto.builder()
                .prompt(prompt)
                .committer(repository.getCommit(commitDetail.getSha()).getAuthor().getLogin())
                .pullRequest(pullRequest)
                .commitDetail(commitDetail)
                .fileCodeLines(fileList.stream()
                        .collect(
                                Collectors.toMap(
                                        CommitFileDto::getFilename,
                                    commitFileDto -> commitFileDto.getCode().split("\n").length
                                )

                        )
                )
                .build());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method that processes each request in the request queue,
   * sends it to OpenAI Api, so it could validate it
   * and leave a review in the PR based on the AI response.
   * Method is scheduled to be called each 21 seconds to prevent quota violation.
   * In the case of invalid response from the OpenAI there is a
   * JsonProcessingException catch statement that returns
   * request back to the queue to be processed again.
   *
   * @throws RuntimeException If github api doesn't find requested objects
   */
  @Scheduled(cron = "*/21 * * * * *")
  private void processValidation() {
    if (!requests.isEmpty()) {
      QueuedValidationProcessDto process = requests.poll();
      String prompt = process.getPrompt();
      GHPullRequest pullRequest = process.getPullRequest();
      GHPullRequestCommitDetail commitDetail = process.getCommitDetail();
      GHPullRequestReviewBuilder review = pullRequest.createReview();
      try {
        String response = openAiService.processPrompt(prompt);
        OpenAiValidationResponseDto openAiResponse = objectMapper.readValue(response,
                OpenAiValidationResponseDto.class);
        try {
          openAiResponse.getFiles().forEach(file -> {
            int codeLines = process.getFileCodeLines().get(file.getFilename());
            file.getComments().forEach(comment -> {
              if (codeLines > comment.getLine()) {
                review.commitId(commitDetail.getSha());
                review.comment(comment.getComment(), file.getFilename(), comment.getLine());
              }
            });
          });
          if (openAiResponse.getRating() >= 7) {
            review.event(GHPullRequestReviewEvent.COMMENT);
          } else {
            review.event(GHPullRequestReviewEvent.REQUEST_CHANGES);
          }
          review.body("OpenAI previous review of commit "
                  + commitDetail.getSha() + ". The rating is "
                  + openAiResponse.getRating() + "/10.");

          review.create();
          notificationService.sendNotificationMessage(
                 process.getCommitter(),
                 String.format(NOTIFICATION_MESSAGE_TEMPLATE, commitDetail.getSha())
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } catch (JsonProcessingException exception) {
        requests.add(process);
      }
    }
  }
}