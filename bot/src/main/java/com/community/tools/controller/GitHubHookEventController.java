package com.community.tools.controller;

import com.community.tools.service.github.event.GitHubHookEventService;
import com.community.tools.service.github.event.TaskStatusChangeEventDispatcher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/gitHook")
@Slf4j
public class GitHubHookEventController {

  private final TaskStatusChangeEventDispatcher eventDispatcher;
  private final GitHubHookEventService gitHubHookEventService;

  /**
   * Method receives and processes webhook event data from GitHub in JSON format.
   *
   * @param body      event body
   * @param eventType header indicating event type
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public void getHookData(@RequestHeader("X-GitHub-Event") String eventType,
      @RequestBody String body) {
    log.info("Web hook received event " + eventType);
    JSONObject eventJson = new JSONObject(body);
    gitHubHookEventService.processGitHubHookEventData(eventJson, eventType)
        .ifPresent(eventDispatcher::dispatchEvent);
  }

}

