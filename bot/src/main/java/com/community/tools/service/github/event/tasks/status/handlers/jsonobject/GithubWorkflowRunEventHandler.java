package com.community.tools.service.github.event.tasks.status.handlers.jsonobject;

import com.community.tools.dto.events.tasks.TaskStatusEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.status.UserTask;
import com.community.tools.model.status.UserTaskId;
import com.community.tools.repository.status.UserTaskRepository;
import com.community.tools.service.github.event.EventHandler;
import com.community.tools.service.github.event.tasks.status.TaskStatusEventProcessingService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubWorkflowRunEventHandler implements EventHandler<JSONObject> {

  private final UserTaskRepository userTaskRepository;
  private final TaskStatusEventProcessingService taskStatusEventProcessingService;
  @Value("${tasksForUsers}")
  private String originalTaskNames;

  @Override
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.has("action") && eventJson.getString("action").equals("completed")) {
      parseAndSaveWorkflowRun(eventJson);
    }
  }

  /**
   * Processes JSON data from GitHub workflow_run event, saves it to the configured database.
   *
   * @param eventJson - representation of the JSON data from GitHub workflow_run event
   * @see <a
   * href="https://docs.github.com/en/webhooks/webhook-events-and-payloads#workflow_run">GitHub docs
   * about the event</a>
   */
  private void parseAndSaveWorkflowRun(JSONObject eventJson) {
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
      return;
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
    //Invoking event processing services after we are sure to have saved the event
    invokeEventHandlers(hasNewChanges, gitName, taskName, pullUrl, isSubmittedFirstTime);
  }

  private void invokeEventHandlers(boolean hasNewChanges, String gitName, String taskName,
      String pullUrl,
      boolean isSubmittedFirstTime) {
    if (hasNewChanges) {
      TaskStatusEventDto dto = TaskStatusEventDto
          .builder()
          .taskStatus(TaskStatus.READY_FOR_REVIEW)
          .withNewChanges(true)
          .traineeGitName(gitName)
          .taskName(taskName)
          .pullUrl(pullUrl)
          .build();
      taskStatusEventProcessingService.processEvent(dto);
    }
    if (isSubmittedFirstTime) {
      TaskStatusEventDto dto = TaskStatusEventDto
          .builder()
          .taskStatus(TaskStatus.READY_FOR_REVIEW)
          .withNewChanges(false)
          .traineeGitName(gitName)
          .taskName(taskName)
          .pullUrl(pullUrl)
          .build();
      taskStatusEventProcessingService.processEvent(dto);
    }
  }

  private boolean checkIfEventIsIrrelevant(JSONObject workflowRun, String taskName) {
    if (!workflowRun.getString("head_branch").equals("feedback")) {
      return true; /* we are only interested in processing changes GitHub Classroom automatically
      adds to the pull request with head branch "feedback" */
    }
    return false;
    //TODO RETURN TO NORMAL
    //return !originalTaskNames.contains(taskName);
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
    //TODO RETURN TO NORMAL
    if (!feedbackPullRequest.isPresent() && false) {
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
}
