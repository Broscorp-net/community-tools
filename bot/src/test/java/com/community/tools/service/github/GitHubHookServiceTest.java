package com.community.tools.service.github;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class GitHubHookServiceTest {

  @MockBean
  StateMachineService stateMachineService;
  @Autowired
  GitHubHookService gitHubHookService;
  @MockBean
  MessageService messageService;



  private static final String USER_NAME = "Some User";


  @Test
  public void sendMessageAboutWrongNamePullRequestTest() {

    when(stateMachineService.getIdByNick(USER_NAME)).thenReturn(USER_NAME);
    when(messageService.getUserById(any())).thenReturn(USER_NAME);

    gitHubHookService.sendMessageAboutWrongNamePullRequest(myJson("BoXing"));

    verify(messageService,times(0)).sendPrivateMessage(USER_NAME, Messages.PULL_REQUEST_WRONG_NAME);

    gitHubHookService.sendMessageAboutWrongNamePullRequest(myJson("bogryng"));

    verify(messageService,times(1)).sendPrivateMessage(USER_NAME, Messages.PULL_REQUEST_WRONG_NAME);

  }

  private JSONObject myJson(String namePullBranch) {
    String jsonStr = "{\"check_run\":{\"check_suite\":{\"head_branch\":\""
        + namePullBranch + "\"}},\"sender\":{\"login\":\"" + USER_NAME + "\"} }";
    return (JSONObject) JSONParser.parseJSON(jsonStr);
  }
}