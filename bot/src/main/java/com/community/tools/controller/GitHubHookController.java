package com.community.tools.controller;

import com.community.tools.service.github.GitHookDataService;
import com.community.tools.service.github.GitHubHookService;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gitHook")
public class GitHubHookController {

  @Value("${github.secret.token}")
  private String secret;
  @Autowired
  private GitHubHookService gitHubHookService;
  @Autowired
  private GitHookDataService gitHookDataService;

  /**
   * Method receive webhook data from GitHub.
   *
   * @param body   String
   * @param header "X-Hub-Signature" header
   * @param resp   HttpServletResponse
   * @throws IOException IOException
   */
  @PostMapping
  public void getHookData(@RequestBody String body,
                          @RequestHeader("X-Hub-Signature") String header,
                          HttpServletResponse resp) {

    JSONObject json = new JSONObject(body);
    gitHookDataService.saveDataIntoDB(json);
    boolean actionExist = false;
    if (json.has("action")) {
      actionExist = true;
    }
    if (actionExist) {
      gitHubHookService.doActionsAfterReceiveHook(json);
    }
  }
}

