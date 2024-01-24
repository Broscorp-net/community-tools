package com.community.tools.slack;

import com.community.tools.model.ServiceUser;
import com.community.tools.service.MessageService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Conversation;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.webhook.Payload;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("slack")
public class SlackService implements MessageService<String> {

  @Value("${slack.token}")
  private String token;

  @Value("${slack.webhook}")
  private String slackWebHook;

  /**
   * Send private message with messageText to username.
   *
   * @param username Slack login
   * @param messageText Text of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendPrivateMessage(String username, String messageText) {
    Slack slack = Slack.getInstance();
    try {
      slack
          .methods(token)
          .chatPostMessage(
            req -> req.channel(getIdByUsername(username)).asUser(true).text(messageText));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send block message with messageText to username.
   *
   * @param username Slack login
   * @param message Text of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendBlocksMessage(String username, String message) {
    Slack slack = Slack.getInstance();
    try {
      slack
          .methods(token)
          .chatPostMessage(req ->
            req.channel(getIdByUsername(username)).asUser(true).blocksAsString(message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void sendBlocksMessage(String username, EmbedBuilder embedBuilder) { }

  /**
   * Send attachment message with messageText to username.
   *
   * @param username Slack login
   * @param message Text of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendAttachmentsMessage(String username, String message) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack
              .methods(token)
              .chatPostMessage(
                  req ->
                      req.channel(getIdByUsername(username))
                          .asUser(true)
                          .attachmentsAsString(message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }


  @Override
  public void sendAttachmentsMessage(String username, EmbedBuilder embedBuilder) { }

  /**
   * Send attachment message with messageText to channel.
   *
   * @param channelName Name of channel
   * @param messageText Text of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendMessageToConversation(String channelName, String messageText) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack
              .methods(token)
              .chatPostMessage(
                  req ->
                      req.channel(getIdByChannelName(channelName)).asUser(true).text(messageText));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Send attachment message with blocks of Text to the channel.
   *
   * @param channelName Name of channel
   * @param message Blocks of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Override
  public void sendBlockMessageToConversation(String channelName, String message) {
    Slack slack = Slack.getInstance();
    try {
      ChatPostMessageResponse postResponse =
          slack
              .methods(token)
              .chatPostMessage(
                  req ->
                      req.channel(getIdByChannelName(channelName))
                          .asUser(true)
                          .blocksAsString(message));
    } catch (IOException | SlackApiException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void sendBlockMessageToConversation(String channelName, EmbedBuilder embedBuilder) { }

  /**
   * Get Conversation by Slack`s channelName.
   *
   * @param channelName Slack`s channelName
   * @return id of Conversation
   */
  @Override
  public String getIdByChannelName(String channelName) {
    Slack slack = Slack.getInstance();
    try {
      Conversation channel =
          slack.methods(token).conversationsList(req -> req).getChannels().stream()
              .filter(u -> u.getName().equals(channelName))
              .findFirst()
              .get();

      return channel.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void addRoleToUser(String guildId, String userId, String newbieRoleName) {

  }

  @Override
  public void removeRole(String guildId, String userId, String newbieRoleName) {

  }

  /**
   * Get user by Slack`s id.
   *
   * @param id Slack`s id
   * @return realName of User
   */
  @Override
  public String getUserById(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user =
          slack.methods(token).usersList(req -> req).getMembers().stream()
              .filter(u -> u.getId().equals(id))
              .findFirst()
              .get();
      return user.getProfile().getDisplayName();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s id.
   *
   * @param id Slack`s id
   * @return Slack`s id
   */
  @Override
  public String getIdByUser(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user =
          slack.methods(token).usersList(req -> req).getMembers().stream()
              .filter(u -> u.getRealName().equals(id))
              .findFirst()
              .get();
      return user.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s username.
   *
   * @param username Slack`s id
   * @return Slack`s id
   */
  @Override
  public String getIdByUsername(String username) {
    Slack slack = Slack.getInstance();
    try {
      User user =
          slack.methods(token).usersList(req -> req).getMembers().stream()
              .filter(u -> u.getProfile().getDisplayName().equals(username))
              .findFirst()
              .get();
      return user.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get all Slack`s user.
   *
   * @return Set of users.
   */
  @Override
  public Set<ServiceUser> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<ServiceUser> users =
          slack
              .methods()
              .usersList(UsersListRequest.builder().token(token).build())
              .getMembers()
              .stream()
              .map(ServiceUser::from)
              .collect(Collectors.toSet());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get id with real name.
   *
   * @return map key id, value real name
   */
  @Override
  public Map<String, String> getIdWithName() {
    return getAllUsers().stream()
        .filter(u -> u.getName() != null)
        .collect(Collectors.toMap(ServiceUser::getId, ServiceUser::getName));
  }

  /**
   * Send announcement with message.
   *
   * @param message Text of message
   */
  @Override
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
