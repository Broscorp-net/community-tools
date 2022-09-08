package com.community.tools.service.github.event;

import org.json.JSONObject;

public interface GithubEventHandler {

  /**
   * Processes GitHub hook event that has been passed.
   *
   * @param eventJson GitHub hook event
   * */
  void handleEvent(JSONObject eventJson);
}
