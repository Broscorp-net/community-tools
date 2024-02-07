package com.community.tools.service.github.event;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.status.UserTask;
import com.community.tools.model.status.UserTaskId;
import com.community.tools.repository.status.UserTaskRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GitHubHookEventService {

  private final UserTaskRepository userTaskRepository;
  @Value("${tasksForUsers}")
  private String originalTaskNames;

  /**
   * Processes JSON data from GitHub webhook events the controller is configured to receive in
   * GitHub organization configurations. Current implementation expects to receive
   * pull_request_review and workflow_run run event types. It analyzes the data it received, updates
   * the current state of user tasks in the configured database and returns an Optional
   * TaskStatusChangeEventDto in case GitHub hook event data indicates a change in the state of one
   * of the user tasks.
   *
   * @param eventType type of the event from X-GitHub-Event header.
   * @param body      parsed JSON body of the request.
   * @return an Optional TaskStatusChangeEventDto with the resulting change in the status of a task
   */
  public Optional<TaskStatusChangeEventDto> processGitHubHookEventData(JSONObject body,
      String eventType) {
    if (eventType.equals("pull_request_review")
        && body.has("action") && body.has("review")) {
      return handlePullRequestReview(body);
    } else if (eventType.equals("workflow_run")
        && body.has("action") && body.getString("action").equals("completed")) {
      return handleWorkflowRun(body);
    }
    return Optional.empty();
  }

  /**
   * Processes JSON data from GitHub pull_request_review event, saves it to the configured
   * database.
   *
   * @param eventJson - representation of the JSON data from GitHub pull_request_review event
   * @return an Optional TaskStatusChangeEventDto with the resulting change in the status of a task
   * @see <a href=
   * "https://docs.github.com/en/webhooks/webhook-events-and-payloads#pull_request_review"> GitHub
   * docs about the event</a>
   */
  private Optional<TaskStatusChangeEventDto> handlePullRequestReview(JSONObject eventJson) {
    boolean wereChangesRequested = false;
    boolean isTaskDoneNow = false;
    final JSONObject review = eventJson.getJSONObject("review");
    final JSONObject reviewerUser = review.getJSONObject("user");
    String reviewerGitName = null;
    if (reviewerUser != null) {
      reviewerGitName = reviewerUser.getString("login");
    }
    final String state = review.getString("state");
    final String pullUrl = review.getString("pull_request_url");
    final Optional<String> maybeTaskName = getTaskNameFromPullUrl(pullUrl);
    if (!maybeTaskName.isPresent()) {
      return Optional.empty();
      //This event is for a pull request unrelated to traineeship in this case
    }
    final String taskName = maybeTaskName.get();
    final String traineeGitName = getTraineeNameFromPullUrl(pullUrl, taskName);
    final Optional<UserTask> maybeUserTask = userTaskRepository.findById(
        new UserTaskId(traineeGitName, taskName));
    if (!maybeUserTask.isPresent()) {
      log.error("Record for a valid task could not be found, recommend user " + traineeGitName
          + " to make another commit or manually re-trigger a workflow on task " + taskName);
      return Optional.empty();
    }
    final UserTask userTask = maybeUserTask.get();
    if (userTask.getTaskStatus().equals(TaskStatus.DONE.getDescription())) {
      return Optional.empty();
    } else if (state.equals("approved")) {
      userTask.setTaskStatus(TaskStatus.DONE.getDescription());
      isTaskDoneNow = true;
    } else if (state.equals("changes_requested") || state.equals("commented")) {
      userTask.setTaskStatus(TaskStatus.CHANGES_REQUESTED.getDescription());
      wereChangesRequested = true;
    }
    userTaskRepository.saveAndFlush(userTask);
    return constructTaskStatusChangeEventDto(wereChangesRequested, traineeGitName, taskName,
        reviewerGitName,
        userTask.getPullUrl(), isTaskDoneNow);
  }

  /**
   * Processes JSON data from GitHub workflow_run event, saves it to the configured database.
   *
   * @param eventJson - representation of the JSON data from GitHub workflow_run event
   * @return an Optional TaskStatusChangeEventDto with the resulting change in the status of a task
   * @see <a
   * href="https://docs.github.com/en/webhooks/webhook-events-and-payloads#workflow_run">GitHub docs
   * about the event</a>
   */
  private Optional<TaskStatusChangeEventDto> handleWorkflowRun(JSONObject eventJson) {
    boolean hasNewChanges = false;
    boolean isSubmittedFirstTime = false;
    final JSONObject repo = eventJson.getJSONObject("repository");
    final JSONObject workflowRun = eventJson.getJSONObject("workflow_run");
    final String gitName = (workflowRun.getJSONObject("actor")).getString(
        "login");
    final String taskName = getTaskName(repo.getString("name"), gitName);
    final String repoFullName = repo.getString("full_name");
    final String conclusion = workflowRun.getString("conclusion");
    final String headCommitId = workflowRun.getJSONObject("head_commit").getString("id");
    if (checkIfEventIsIrrelevant(workflowRun, taskName)) {
      return Optional.empty();
    }
    validateGithubClassroomPullRequestExistence(workflowRun, gitName, taskName);
    Optional<UserTask> existingUserTaskRecord = userTaskRepository.findById(
        new UserTaskId(gitName, taskName));
    UserTask record;
    if (existingUserTaskRecord.isPresent()) {
      record = existingUserTaskRecord.get();
      if (!conclusion.equals("success") && !record.getTaskStatus()
          .equals(TaskStatus.DONE.getDescription())) {
        record.setTaskStatus(TaskStatus.FAILURE.getDescription());
      } else if (conclusion.equals("success") && !record.getTaskStatus()
          .equals(TaskStatus.DONE.getDescription())) {
        hasNewChanges = containsNewChanges(workflowRun, headCommitId);
      }
    } else {
      record = new UserTask();
      record.setGitName(gitName);
      record.setTaskName(taskName);
      if (conclusion.equals("success")) {
        record.setTaskStatus(TaskStatus.READY_FOR_REVIEW.getDescription());
        isSubmittedFirstTime = true;
      } else {
        record.setTaskStatus(TaskStatus.FAILURE.getDescription());
      }
    }
    final String pullUrl = formPullUrl(repoFullName);
    record.setLastActivity(LocalDate.now());
    record.setPullUrl(pullUrl);
    record.setHeadCommitId(headCommitId);

    userTaskRepository.saveAndFlush(record);
    return constructTaskStatusChangeEventDto(
        hasNewChanges, gitName, taskName, pullUrl, isSubmittedFirstTime);
  }

  private static Optional<TaskStatusChangeEventDto> constructTaskStatusChangeEventDto(
      boolean hasNewChanges, String gitName, String taskName, String pullUrl,
      boolean isSubmittedFirstTime) {
    if (hasNewChanges) {
      TaskStatusChangeEventDto dto = TaskStatusChangeEventDto
          .builder()
          .taskStatus(TaskStatus.READY_FOR_REVIEW)
          .withNewChanges(true)
          .traineeGitName(gitName)
          .taskName(taskName)
          .pullUrl(pullUrl)
          .build();
      return Optional.of(dto);
    } else if (isSubmittedFirstTime) {
      TaskStatusChangeEventDto dto = TaskStatusChangeEventDto
          .builder()
          .taskStatus(TaskStatus.READY_FOR_REVIEW)
          .withNewChanges(false)
          .traineeGitName(gitName)
          .taskName(taskName)
          .pullUrl(pullUrl)
          .build();
      return Optional.of(dto);
    }
    return Optional.empty();
  }

  private static Optional<TaskStatusChangeEventDto> constructTaskStatusChangeEventDto(
      boolean wereChangesRequested, String traineeGitName, String taskName, String reviewerGitName,
      String pullUrl, boolean isTaskDoneNow) {
    if (wereChangesRequested) {
      TaskStatusChangeEventDto dto = TaskStatusChangeEventDto
          .builder()
          .taskStatus(TaskStatus.CHANGES_REQUESTED)
          .withNewChanges(false)
          .traineeGitName(traineeGitName)
          .taskName(taskName)
          .reviewerGitName(reviewerGitName)
          .pullUrl(pullUrl)
          .build();
      return Optional.of(dto);
    } else if (isTaskDoneNow) {
      TaskStatusChangeEventDto dto = TaskStatusChangeEventDto
          .builder()
          .taskStatus(TaskStatus.DONE)
          .withNewChanges(false)
          .traineeGitName(traineeGitName)
          .taskName(taskName)
          .reviewerGitName(reviewerGitName)
          .pullUrl(pullUrl)
          .build();
      return Optional.of(dto);
    }
    return Optional.empty();
  }

  /**
   * Determines whether we are not interested in processing this workflow run.
   * We are only interested in processing changes GitHub Classroom automatically
   * adds to the pull request with head branch "master" into base branch "feedback", commits by
   * trainees should be made into master as by GitHub classroom instructions.
   */
  private boolean checkIfEventIsIrrelevant(JSONObject workflowRun, String taskName) {
    if (!workflowRun.getString("head_branch").equals("master")) {
      return true;
    }
    return !originalTaskNames.contains(taskName);
  }

  private static boolean containsNewChanges(JSONObject workflowRun, String headCommitId) {
    return workflowRun.getJSONObject("head_commit").getString("id")
        .equals(headCommitId);
  }

  private static void validateGithubClassroomPullRequestExistence(JSONObject workflowRun,
      String gitName, String taskName) {
    final Optional<HashMap> feedbackPullRequest = (workflowRun.getJSONArray(
            "pull_requests").toList().stream().map(it -> (HashMap) it)
        .filter(it -> (Integer) it.get("number") == 1).findFirst());
    if (!feedbackPullRequest.isPresent()) {
      String errorMessage =
          "Could not find Feedback pull request from GitHub classroom for user " + gitName
              + " doing task " + taskName;
      log.error(errorMessage);
      throw new RuntimeException(errorMessage);
    }
  }

  private static String formPullUrl(String repoFullName) {
    return "https://github.com/" + repoFullName + "/pull/1";
  }

  private static String getTaskName(String repoName, String gitName) {
    int gitNameIndex = repoName.indexOf("-" + gitName);
    if (gitNameIndex != -1) {
      return repoName.substring(0, gitNameIndex);
    }
    return repoName;
  }

  private Optional<String> getTaskNameFromPullUrl(final String pullUrl) {
    List<String> splitTaskNames = Arrays.stream(originalTaskNames.split(","))
        .map(String::trim)
        .filter(it -> !it.isEmpty())
        .collect(Collectors.toList());
    return splitTaskNames.stream().filter(pullUrl::contains)
        .findFirst();
  }

  private static String getTraineeNameFromPullUrl(final String pullUrl, final String currentTask) {
    int pullsIdIndex = pullUrl.indexOf("/pulls/");
    final String pullUrlWithNoPullId = pullUrl.substring(0, pullsIdIndex);
    final String fullRepoName = pullUrlWithNoPullId.substring(
        pullUrlWithNoPullId.indexOf(currentTask));
    return fullRepoName.substring(currentTask.length() + 1);
  }
}
