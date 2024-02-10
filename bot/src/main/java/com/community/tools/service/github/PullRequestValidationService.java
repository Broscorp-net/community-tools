package com.community.tools.service.github;

import com.community.tools.dto.OpenAiValidationResponseDto;
import com.community.tools.dto.PullrequestPatchDto;
import com.community.tools.dto.QueuedValidationProcessDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.service.NotificationService;
import com.community.tools.service.openai.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import org.kohsuke.github.GHPullRequest;
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
  private static final String PROMPT_TEMPLATE = "Rate the provided as git patch "
          + "code(line with - is deleted line and + is added) on a scale from 1 to 10, "
          + "considering its quality, readability and efficiency. "
          + "Additionally, provide constructive comments for improvement, "
          + "but avoid duplicating comments with the same issues and sense. "
          + "Please return the feedback in the following format:"
          + "{"
          + "  \"rating\": ${rating_integer},"
          + "  \"files\": ["
          + "    {"
          + "      \"filename\": \"${filename}\","
          + "      \"comments\": ["
          + "        {"
          + "          \"line\": ${line_number},"
          + "          \"comment\": \"${comment}\""
          + "        }"
          + "      ]"
          + "    }"
          + "  ]"
          + "}"
          + "The code is provided in json format [{filename: ${filename}, code: ${code}}]. "
          + "The code is: %s";

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
   * and gathers all PR files into one prompt to put it in the queue of validation.
   *
   * @param repositoryName name of the repository that needs its PR to be validated.
   * @throws RuntimeException If github api doesn't find requested objects
   */
  public void validatePullRequest(String repositoryName, String traineeGitName) {
    GHRepository repository = gitService.getGitHubRepositoryByName(repositoryName);
    try {
      GHPullRequest pullRequest = repository.getPullRequest(1);

      List<PullrequestPatchDto> fileList = new ArrayList<>();
      pullRequest.listFiles().forEach(
              file -> fileList.add(
                      new PullrequestPatchDto(file.getFilename(), file.getPatch()))
      );

      String prompt = String.format(PROMPT_TEMPLATE, objectMapper.writeValueAsString(fileList));
      requests.add(QueuedValidationProcessDto.builder()
              .prompt(prompt)
              .traineeGitName(traineeGitName)
              .pullRequest(pullRequest)
              .fileCodeLines(fileList.stream()
                      .collect(
                              Collectors.toMap(
                                      PullrequestPatchDto::getFilename,
                                      pullrequestPatchDto ->
                                              pullrequestPatchDto.getCode().split("\n").length
                              )
                      )
              )
              .build());
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
                review.comment(comment.getComment(), file.getFilename(), comment.getLine());
              }
            });
          });
          GHPullRequestReviewEvent taskStatus = openAiResponse.getRating() >= 7
                  ? GHPullRequestReviewEvent.COMMENT : GHPullRequestReviewEvent.REQUEST_CHANGES;

          review.event(taskStatus);
          review.body("OpenAI previous review of the pullrequest. "
                  + "The rating is "
                  + openAiResponse.getRating() + "/10.");
          review.create();

          notificationService.sendPullRequestUpdateNotification(
                  process.getTraineeGitName(),
                  List.of(new TaskNameAndStatus(
                          pullRequest.getBase().getRepository().getName(),
                          pullRequest.getUrl().toString(),
                          taskStatus.toString())
                  ));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } catch (JsonProcessingException exception) {
        requests.add(process);
      }
    }
  }
}