package com.community.tools.service;

import net.dv8tion.jda.api.EmbedBuilder;

public interface MessageService<T> extends UserService {

  void sendPrivateMessage(String username, String messageText);

  void sendBlocksMessage(String username, T message);

  void sendBlocksMessage(String username, EmbedBuilder embedBuilder);

  void sendAttachmentsMessage(String username, T message);

  void sendAttachmentsMessage(String username, EmbedBuilder embedBuilder);

  void sendMessageToConversation(String channelName, String messageText);

  void sendBlockMessageToConversation(String channelName, T message);

  void sendBlockMessageToConversation(String channelName, EmbedBuilder embedBuilder);

  void sendAnnouncement(String message);

  String getIdByChannelName(String channelName);

}
