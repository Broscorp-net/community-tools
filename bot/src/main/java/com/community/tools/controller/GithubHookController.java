package com.community.tools.controller;

import com.community.tools.service.github.GitHookDataService;
import com.community.tools.service.github.event.GithubEventsProcessingService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/gitHook")
public class GithubHookController {

  private final GithubEventsProcessingService eventsProcessingService;
  private final GitHookDataService gitHookDataService;

  /**
   * Method receive webhook data from GitHub.
   *
   * @param body event body
   */
  @PostMapping
  public void getHookData(@RequestBody String body) {
    JSONObject eventJson = new JSONObject(body);
    gitHookDataService.saveDataIntoDB(eventJson);
    if (eventJson.has("action")) {
      eventsProcessingService.processEvent(eventJson);
    }
  }
}

