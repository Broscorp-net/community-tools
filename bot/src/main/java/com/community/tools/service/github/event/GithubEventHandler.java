package com.community.tools.service.github.event;

import org.json.JSONObject;

public interface GithubEventHandler {

  void handleEvent(JSONObject eventJson);
}
