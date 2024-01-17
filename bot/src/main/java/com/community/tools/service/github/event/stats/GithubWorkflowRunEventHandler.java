package com.community.tools.service.github.event.stats;

import com.community.tools.service.github.event.GithubEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class GithubWorkflowRunEventHandler implements GithubEventHandler {

  @Override
  // repository->name - task name
  // workflow_run->actor->login - user git name
  // action key should be "completed"
  // pull request number pull_requests->(list of objects, get(0), form into json)->number
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.getString("action").equals("completed")) {
      log.info(((JSONObject) eventJson.get("repository")).getString("name"));
      log.info(((JSONObject) eventJson.get("repository")).getString("full_name"));
      log.info(((JSONObject) ((JSONObject) eventJson.get("workflow_run")).get("actor")).getString(
          "login"));
      int pullId = ((JSONObject) ((JSONObject) eventJson.get("workflow_run")).getJSONArray(
          "pull_requests").get(0)).getInt("number");
      log.info(pullId + "");
    }
  }
}
