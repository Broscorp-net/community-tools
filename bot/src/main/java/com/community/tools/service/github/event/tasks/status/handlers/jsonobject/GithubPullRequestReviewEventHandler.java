package com.community.tools.service.github.event.tasks.status.handlers.jsonobject;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.status.UserTask;
import com.community.tools.model.status.UserTaskId;
import com.community.tools.repository.status.UserTaskRepository;
import com.community.tools.service.github.event.EventHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubPullRequestReviewEventHandler implements EventHandler<JSONObject> {

  private final UserTaskRepository userTaskRepository;
  @Value("${tasksForUsers}")
  private String originalTaskNames;

  @Override
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.has("action") && eventJson.has("review")) {
      handlePullRequestReview(eventJson);
    }
  }

  /**
   * Processes JSON data from GitHub pull_request_review event, saves it to the configured
   * database.
   *
   * @param eventJson - representation of the JSON data from GitHub pull_request_review event
   * @see <a href=
   * "https://docs.github.com/en/webhooks/webhook-events-and-payloads#pull_request_review"> GitHub
   * docs about the event</a>
   */
  private void handlePullRequestReview(JSONObject eventJson) {
    final JSONObject review = eventJson.getJSONObject("review");
    final String state = review.getString("state");
    final String pullUrl = review.getString("pull_request_url");
    final Optional<String> maybeTaskName = getTaskNameFromPullUrl(pullUrl);
    if (!maybeTaskName.isPresent()) {
      return; //This event is for a pull request unrelated to traineeship in this case
    }
    final String taskName = maybeTaskName.get();
    final String traineeGitName = getTraineeNameFromPullUrl(pullUrl, taskName);
    final Optional<UserTask> maybeUserTask = userTaskRepository.findById(
        new UserTaskId(traineeGitName, taskName));
    if (!maybeUserTask.isPresent()) {
      log.error("Record for a valid task could not be found, recommend user " + traineeGitName
          + " to make another commit or manually re-trigger a workflow on task " + taskName);
      return;
    }
    final UserTask userTask = maybeUserTask.get();
    if (userTask.getTaskStatus().equals(TaskStatus.DONE.getDescription())) {
      return;
    } else if (state.equals("approved")) {
      userTask.setTaskStatus(TaskStatus.DONE.getDescription());
    } else if (state.equals("changes_requested") || state.equals("commented")) {
      userTask.setTaskStatus(TaskStatus.CHANGES_REQUESTED.getDescription());
    }
    userTaskRepository.saveAndFlush(userTask);
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
