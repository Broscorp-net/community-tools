package com.community.tools.service.slack;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.webhook.Payload;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackService {

  @Value("${slack.token}")
  private String token;
  @Value("${slack.webhook}")
  private String slackWebHook;

  public String sendPrivateMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .text(messageText));

    return postResponse.getTs();
  }

  public String sendBlocksMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .blocksAsString(messageText));

    return postResponse.getTs();
  }

  public String sendAttachmentsMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .attachmentsAsString(messageText));

    return postResponse.getTs();
  }

  public String sendMessageToChat(String channelName, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    Channel channel = slack.methods(token)
        .channelsList(req -> req)
        .getChannels()
        .stream()
        .filter(u -> u.getName().equals(channelName))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(channel.getId()).asUser(true).text(messageText));

    return postResponse.getTs();
  }

  public String getUserById(String id){
    Slack slack = Slack.getInstance();
    try {
     User user = slack.methods(token).usersList(req -> req).getMembers().stream()
          .filter(u -> u.getId().equals(id))
          .findFirst().get();
     return user.getRealName();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
  public Set<User> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<User> users = new HashSet<>(slack.methods()
          .usersList(UsersListRequest.builder()
              .token(token)
              .build())
          .getMembers());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendAnnouncement(String message) {
    try {
      Payload payload = Payload.builder().text(message).build();
      Slack slack = Slack.getInstance();
      slack.send(slackWebHook, payload);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}