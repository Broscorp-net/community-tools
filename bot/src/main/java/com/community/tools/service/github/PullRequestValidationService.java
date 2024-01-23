package com.community.tools.service.github;

import com.community.tools.dto.CommitFileDTO;
import com.community.tools.dto.OpenAIValidationResponseDTO;
import com.community.tools.dto.QueuedValidationProcessDTO;
import com.community.tools.service.openai.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PullRequestValidationService {
    private final GitHubConnectService gitService;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAIService;
    private final Queue<QueuedValidationProcessDTO> requests = new LinkedList<>();

    public PullRequestValidationService(GitHubConnectService gitService, ObjectMapper objectMapper, OpenAiService openAIService) {
        this.gitService = gitService;
        this.objectMapper = objectMapper;
        this.openAIService = openAIService;
    }

    /**
     * Main method of the service that retrieves Feedback PR by its id(1), iterates through its commits(except those by bot created)
     * and gathers all commit files into one prompt to put it in the queue of validation.
     *
     * @param repositoryName name of the repository that needs its PR to be validated.
     *
     * @throws RuntimeException If github api doesn't find requested objects
     */
    public void validatePullRequest(String repositoryName) {
        GHRepository repository = gitService.getGitHubRepositoryByName(repositoryName);
        try {
            GHPullRequest pullRequest = repository.getPullRequest(1);

            for (GHPullRequestCommitDetail commitDetail : pullRequest.listCommits().toList().stream().filter(commit -> !commit.getCommit().getAuthor().getName().equals("github-classroom[bot]")).collect(Collectors.toList())) {
                List<CommitFileDTO> fileList = new ArrayList<>();
                for (GHCommit.File file : repository.getCommit(commitDetail.getSha()).getFiles().stream().filter(Objects::nonNull).collect(Collectors.toList())) {
                    fileList.add(new CommitFileDTO(file.getFileName(), file.getPatch()));
                }
                String prompt = "Provide me with the code review where you point out lines of code that have some issues and make an comment what are they and why. " +
                        "Then rate me the code on the scale from 1 to 10 where 1 is the poorest. " +
                        "Rate code considering it's trainee developer.The code will be provided in JSON format as [{filename: ${filename}, code: ${code}}]. Ignore git patch headers. The code to rate is:" + objectMapper.writeValueAsString(fileList) + ". Return me response in the JSON format of {" +
                        "  rating: ${rating_integer}," +
                        "  files: [" +
                        "    filename: ${filename}," +
                        "    comments: [" +
                        "      {" +
                        "        line: ${line_number_integer}," +
                        "        comment: ${comment}" +
                        "      }" +
                        "    ]" +
                        "  ]" +
                        "} return only strictly json response.";

                requests.add(QueuedValidationProcessDTO.builder()
                        .prompt(prompt)
                        .pullRequest(pullRequest)
                        .commitDetail(commitDetail)
                        .fileCodeLines(fileList.stream()
                                .collect(Collectors.toMap(CommitFileDTO::getFilename, commitFileDTO -> commitFileDTO.getCode().split("\n").length)))
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that processes each request in the request queue, sends it to OpenAI Api, so it could validate it
     * and leave a review in the PR based on the AI response.
     * Method is scheduled to be called each 21 seconds to prevent quota violation.
     *
     * @throws RuntimeException If github api doesn't find requested objects
     */
    @Scheduled(cron = "*/21 * * * * *")
    private void processValidation(){
        if(!requests.isEmpty()){
            QueuedValidationProcessDTO process = requests.poll();
            String prompt = process.getPrompt();
            GHPullRequest pullRequest = process.getPullRequest();
            GHPullRequestCommitDetail commitDetail = process.getCommitDetail();
            GHPullRequestReviewBuilder review = pullRequest.createReview();
            try {
                String response = openAIService.processPrompt(prompt);
                OpenAIValidationResponseDTO openAIValidationResponseDTO = objectMapper.readValue(response, OpenAIValidationResponseDTO.class);
                try {
                    openAIValidationResponseDTO.getFiles().forEach(file-> {
                        int codeLines = process.getFileCodeLines().get(file.getFilename());
                        file.getComments().forEach(comment -> {
                            if(codeLines > comment.getLine()){
                                review.commitId(commitDetail.getSha());
                                review.comment(comment.getComment(), file.getFilename(), comment.getLine());
                            }
                        });
                    });
                    if(openAIValidationResponseDTO.getRating() > 7){
                        review.event(GHPullRequestReviewEvent.COMMENT);
                    }
                    else{
                        review.event(GHPullRequestReviewEvent.REQUEST_CHANGES);
                    }
                    review.body("OpenAI previous review of commit " + commitDetail.getSha() + ". The rating is " + openAIValidationResponseDTO.getRating() + "/10.");

                    review.create();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (JsonProcessingException E){
                requests.add(process);
            }
        }
    }
}