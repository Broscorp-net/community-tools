package com.community.tools.service.github.event.stats;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.stats.UserTask;
import com.community.tools.model.stats.UserTaskId;
import com.community.tools.repository.stats.UserTaskRepository;
import com.community.tools.service.github.event.GithubEventHandler;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("discord")
public class GithubWorkflowRunEventHandler implements GithubEventHandler {

  private final UserTaskRepository userTaskRepository;

  @Override
  // repository->name - task name
  // workflow_run->actor->login - user git name
  // action key should be "completed"
  // pull request number pull_requests->(list of objects, get(0), form into json)->number
  // workflow_run->conclusion - result of the workflow
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.has("action") && eventJson.getString("action").equals("completed")) {
      parseAndSaveWorkflowRun(eventJson);
    }
  }

  private void parseAndSaveWorkflowRun(JSONObject eventJson) {
    JSONObject repo = (JSONObject) eventJson.get("repository");
    JSONObject workflowRun = (JSONObject) eventJson.get("workflow_run");
    String gitName = ((JSONObject) workflowRun.get("actor")).getString(
        "login");
    String taskName = getTaskName(repo.getString("name"), gitName);
    String repoFullName = repo.getString("full_name");
    String conclusion = workflowRun.getString("conclusion");
    int pullId = ((JSONObject) workflowRun.getJSONArray(
        "pull_requests").get(0)).getInt("number");
    Optional<UserTask> existingUserTaskRecord = userTaskRepository.findById(
        new UserTaskId(gitName, taskName));
    UserTask record;
    if (existingUserTaskRecord.isPresent()) {
      record = existingUserTaskRecord.get();
    } else {
      record = new UserTask();
      record.setGitName(gitName);
      record.setPullUrl(formPullUrl(pullId, repoFullName));
      record.setTaskName(taskName);
    }
    record.setLastActivity(LocalDate.now());
    record.setPullUrl(formPullUrl(pullId, repoFullName));
    if (conclusion.equals("success")) {
      record.setTaskStatus(TaskStatus.READY_FOR_REVIEW.getDescription());
    } else {
      record.setTaskStatus(TaskStatus.FAILURE.getDescription());
    }
    userTaskRepository.saveAndFlush(record);
  }

  private static String formPullUrl(int pullNumber, String repoFullName) {
    return "https://github.com/" + repoFullName + "pull/" + pullNumber;
  }

  private static String getTaskName(String repoName, String gitName) {
    int gitNameIndex = repoName.indexOf("-" + gitName);
    if (gitNameIndex != -1) {
      return repoName.substring(0, gitNameIndex);
    }
    return repoName;
  }
}
