package com.community.tools.discord;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Data
@Profile("discord")
public class DiscordMessageService {

  private static final int MAX_CHARACTERS = 6000;
  private static final int MAX_MESSAGE_ELEMENTS = 25;

  private JDA jda;

  @Autowired
  public DiscordMessageService(JDA jda) {
    this.jda = jda;
  }

  /**
   * Sends a Discord message with embedded messages and a button in the specified text channel. If
   * the message is too long and gets split into multiple parts, the button is added to the last
   * part.
   *
   * @param channelId The identifier of the channel to send the message to.
   * @param text The initial MessageEmbed to be sent.
   * @param fields The list of fields for the message (supports MessageEmbed).
   */
  protected void sendDiscordMessage(
      String channelId, MessageEmbed text, List<MessageEmbed.Field> fields) {
    TextChannel textChannel = jda.getTextChannelById(channelId);

    Button button = Button.primary("buttonEmbed", "Button");
    List<MessageEmbed> messageChunks;

    if (isCharacterLimitExceeded(text)) {
      messageChunks = splitMessageByCharacterLimit(fields);
    } else if (fields.size() > MAX_MESSAGE_ELEMENTS) {
      messageChunks = splitMessageByFieldLimit(fields);
    } else {
      sendEmbedWithButton(textChannel, text, button);
      return;
    }

    for (int i = 0; i < messageChunks.size(); i++) {
      MessageEmbed chunk = messageChunks.get(i);
      if (i == messageChunks.size() - 1) {
        sendEmbedWithButton(textChannel, chunk, button);
      } else {
        textChannel.sendMessageEmbeds(chunk).queue();
      }
    }
  }

  /**
   * Splits a list of message fields into multiple parts with consideration for the field limit
   * (25).
   *
   * @param fields The list of message fields.
   * @return A list of message parts, each containing up to 25 fields.
   */
  private List<MessageEmbed> splitMessageByFieldLimit(List<MessageEmbed.Field> fields) {
    List<MessageEmbed> messageChunks = new ArrayList<>();

    for (int i = 0; i < fields.size(); i += 25) {
      int endIndex = Math.min(i + 25, fields.size());
      List<MessageEmbed.Field> chunkFields = fields.subList(i, endIndex);
      EmbedBuilder chunkBuilder = new EmbedBuilder();
      chunkFields.forEach(chunkBuilder::addField);
      messageChunks.add(chunkBuilder.build());
    }

    return messageChunks;
  }

  /**
   * Splits a list of message fields into multiple parts with consideration for the character limit.
   *
   * @param fields The list of message fields.
   * @return A list of message parts, each fitting within the character limit.
   */
  private List<MessageEmbed> splitMessageByCharacterLimit(List<MessageEmbed.Field> fields) {
    List<MessageEmbed> messageChunks = new ArrayList<>();
    EmbedBuilder currentChunk = new EmbedBuilder();
    int fieldCount = 0;
    int characterCount = 0;

    for (MessageEmbed.Field field : fields) {
      if (fieldCount >= MAX_MESSAGE_ELEMENTS
          || characterCount + field.toString().length() > MAX_CHARACTERS) {
        messageChunks.add(currentChunk.build());
        currentChunk.clearFields();
        fieldCount = 0;
        characterCount = 0;
      }

      if (field.toString().length() > MAX_CHARACTERS) {
        String fieldValue = field.toString();
        while (fieldValue.length() > MAX_CHARACTERS) {
          currentChunk.addField(
              field.getName(), fieldValue.substring(0, MAX_CHARACTERS), field.isInline());
          messageChunks.add(currentChunk.build());
          currentChunk.clearFields();
          fieldCount = 0;
          fieldValue = fieldValue.substring(MAX_CHARACTERS);
          characterCount = 0;
        }
        currentChunk.addField(field.getName(), fieldValue, field.isInline());
      } else {
        currentChunk.addField(field);
      }

      fieldCount++;
      characterCount += field.toString().length();
    }

    if (fieldCount > 0) {
      messageChunks.add(currentChunk.build());
    }

    return messageChunks;
  }

  /**
   * Checks if the character limit for a message is exceeded.
   *
   * @param message The message with fields to check.
   * @return true if the limit is exceeded, false otherwise.
   */
  private boolean isCharacterLimitExceeded(MessageEmbed message) {
    int totalCharacterCount = 0;
    for (MessageEmbed.Field field : message.getFields()) {
      totalCharacterCount += field.getValue().length();
    }
    return totalCharacterCount > MAX_CHARACTERS;
  }

  /**
   * Sends a Discord message with an embedded message and a button in the specified text channel.
   *
   * @param textChannel The TextChannel where the message should be sent.
   * @param embed The MessageEmbed to include in the message.
   * @param button The Button to be added to the message.
   */
  private void sendEmbedWithButton(TextChannel textChannel, MessageEmbed embed, Button button) {
    textChannel.sendMessageEmbeds(embed).setActionRows(ActionRow.of(button)).queue();
  }
}
