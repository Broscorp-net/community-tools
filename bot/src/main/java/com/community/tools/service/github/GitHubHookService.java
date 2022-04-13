package com.community.tools.service.github;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.PointsTaskService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.util.statemachine.Event;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubHookService {

  @Value("${git.check.label}")
  private String labeledStr;
  @Value("${git.check.new.req}")
  private String opened;
  @Value("${generalInformationChannel}")
  private String channel;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;


  @Autowired
  private MessageConstructor messageConstructor;
  @Autowired
  private AddMentorService addMentorService;
  @Autowired
  private StateMachineService stateMachineService;
  @Autowired
  private KarmaService karmaService;
  @Autowired
  private PointsTaskService pointsTaskService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private TaskStatusService taskStatusService;

  /**
   * Methid receive data from Github and check it.
   *
   * @param json JSON with data from Github webhook
   */
  public void doActionsAfterReceiveHook(JSONObject json) {
    sendNotificationMessageAboutPR(json);
    sendMessageAboutFailedBuild(json);
    sendMessageAboutWrongNamePullRequest(json);
    giveNewTaskIfPrOpened(json);
    addMentorIfEventIsReview(json);
    addPointIfPullLabeledDone(json);
    checkReactionToChangeKarma(json);
    taskStatusService.updateTasksStatus(json);
  }


  private void sendNotificationMessageAboutPR(JSONObject json) {
    if (json.get("action").toString().equals(opened) || checkForLabeled(json)) {
      JSONObject pull = json.getJSONObject("pull_request");
      String user = pull.getJSONObject("user").getString("login");
      String url = pull.getJSONObject("_links").getJSONObject("html").getString("href");
      if (addMentorService.doesMentorExist(user)) {
        try {
          addMentorService.sendNotifyWithMentor(user, url);
        } catch (IOException | SlackApiException e) {
          throw new RuntimeException(e);
        }
      } else {
        messageService.sendMessageToConversation(channel,
                  "User " + user
                          + " created a pull request \n url: " + url);
      }
    }
  }

  private boolean checkForLabeled(JSONObject json) {
    if (json.get("action").toString().equals(labeledStr)) {
      List<Object> list = json.getJSONObject("pull_request").getJSONArray("labels").toList();
      return list.stream().map(o -> (HashMap) o)
              .anyMatch(e -> e.get("name").equals("ready for review"));
    }
    return false;
  }


  private void addMentorIfEventIsReview(JSONObject json) {
    if (json.get("action").equals("submitted") || checkComment(json)) {
      String mentor;
      String creator;
      try {
        mentor = json.getJSONObject("comment").getJSONObject("user").getString("login");
      } catch (JSONException e) {
        mentor = json.getJSONObject("review").getJSONObject("user").getString("login");
      }
      try {
        creator = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      } catch (JSONException e) {
        creator = json.getJSONObject("issue").getJSONObject("user").getString("login");
      }


      addMentorService.addMentor(mentor, creator);
    }
  }

  private void addPointIfPullLabeledDone(JSONObject json) {
    if (json.get("action").toString().equals(labeledStr)
            && json.getJSONObject("label").getString("name").equals("done")) {
      List<Object> list = json.getJSONObject("pull_request").getJSONArray("labels").toList();
      String sender = json.getJSONObject("sender").getString("login");
      String creator = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      String pullName = json.getJSONObject("pull_request").getString("title");
      pointsTaskService.addPointForCompletedTask(sender, creator, pullName);
    }
  }

  private void addKarmaForCommentApproved(JSONObject json) {
    boolean checkCommentApproved = false;
    String traineeReviewer = "";
    if (json.get("action").equals("created") && hasIssueAndComment(json)) {
      traineeReviewer = json.getJSONObject("comment").getJSONObject("user").getString("login");
      checkCommentApproved = json.getJSONObject("comment")
              .getString("body").equalsIgnoreCase("approved");
    } else if (json.get("action").equals("submitted")) {
      traineeReviewer = json.getJSONObject("review").getJSONObject("user").getString("login");
      if (json.getJSONObject("review").getString("body") != null) {
        checkCommentApproved = json.getJSONObject("review")
                .getString("body").equalsIgnoreCase("approved");
      }
    }
    if (checkCommentApproved) {
      karmaService.changeUserKarma(traineeReviewer, 1);
    }
  }

  private void checkReactionToChangeKarma(JSONObject json) {
    if (json.get("action").equals(labeledStr)
            && json.getJSONObject("label").getString("name").equals("done")) {
      int numberOfPullRequest = json.getInt("number");
      karmaService.changeKarmaBasedOnReaction(numberOfPullRequest);
    }
  }

  private boolean hasIssueAndComment(JSONObject json) {
    boolean checkIssue = false;
    if (checkComment(json)) {
      checkIssue = json.has("issue");
    }
    return checkIssue;
  }

  private boolean checkComment(JSONObject json) {
    boolean checkComment = false;
    try {
      json.getJSONObject("comment");
      checkComment = true;
    } catch (JSONException e) {
      e.getMessage();
    }
    return checkComment;
  }

  private void giveNewTaskIfPrOpened(JSONObject json) {
    if (json.get("action").toString().equals(opened)) {
      String userNick = json.getJSONObject("sender").getString("login");

      String userId = stateMachineService.getIdByNick(userNick);
      stateMachineService
          .doAction(stateMachineService.restoreMachineByNick(userNick), new SimplePayload(userId),
              Event.SEND_ESTIMATE_TASK);
    }
  }

  private void sendMessageAboutFailedBuild(JSONObject json) {
    if (json.get("action").toString().equals("completed") && json.has("check_run")) {
      JSONObject checkRun = json.getJSONObject("check_run");
      if (checkRun.getString("conclusion").equals("failure")) {

        String url = checkRun.getString("html_url");
        String task = checkRun.getJSONObject("check_suite").getString("head_branch");
        String userNick = json.getJSONObject("sender").getString("login");
        String userId = stateMachineService.getIdByNick(userNick);
        messageService.sendBlocksMessage(messageService.getUserById(userId),
            messageConstructor.createFailedBuildMessage(url, task));
      }
    }
  }

  private void sendMessageAboutWrongNamePullRequest(JSONObject json) {
    JSONObject checkRun = json.getJSONObject("check_run");
    String task = checkRun.getJSONObject("check_suite").getString("head_branch");
    String userNick = json.getJSONObject("sender").getString("login");
    String userId = stateMachineService.getIdByNick(userNick);
    boolean taskIs = Arrays.stream(tasksForUsers).filter(t -> t.equals(task)).toArray().length == 1;
    if (taskIs) {
      messageService.sendPrivateMessage(messageService.getUserById(userId),
              Messages.PULL_REQUEST_WRONG_NAME);
    }
  }

}
