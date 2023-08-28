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
public class DiscordMessagingService {

  private static final int MAX_CHARACTERS = 3500;
  private static final int MAX_MESSAGE_ELEMENTS = 25;

  @Autowired
  private JDA jda;

  /**
   * Splits and sends an EmbedBuilder's content to a text channel with the option to include a
   * Button. If the content exceeds character or field limits, it is divided into smaller messages.
   *
   * @param channelId The ID of the target text channel.
   * @param embedBuilder The EmbedBuilder containing the message's embedded content.
   * @param button The Button to include in the last message chunk, or null if no button is needed.
   */
  public void splitAndSendEmbed(String channelId, EmbedBuilder embedBuilder, Button button) {
    TextChannel textChannel = jda.getTextChannelById(channelId);

    if (isEmbedBuilderTooLarge(embedBuilder)
        || embedBuilder.getFields().size() > MAX_MESSAGE_ELEMENTS) {
      List<EmbedBuilder> embedChunks = splitEmbedBuilder(embedBuilder);

      for (int i = 0; i < embedChunks.size(); i++) {
        EmbedBuilder chunk = embedChunks.get(i);
        if (i == embedChunks.size() - 1 && button != null) {
          sendEmbedWithButton(textChannel, chunk.build(), button);
        } else {
          sendEmbed(textChannel, chunk.build());
        }
      }
    } else {
      sendEmbedWithButton(textChannel, embedBuilder.build(), button);
    }
  }

  /**
   * Checks if an EmbedBuilder's content is too large to send in a single message.
   *
   * @param embedBuilder The EmbedBuilder to check.
   * @return True if the content is too large, otherwise false.
   */
  private boolean isEmbedBuilderTooLarge(EmbedBuilder embedBuilder) {
    try {
      int totalCharacterCount = calculateEmbedLength(embedBuilder);

      return totalCharacterCount > MAX_CHARACTERS;
    } catch (Exception e) {
      return true;
    }
  }

  /**
   * Calculates the length of an EmbedBuilder's content in characters.
   *
   * @param embedBuilder The EmbedBuilder to calculate the length for.
   * @return The total character count.
   */
  private int calculateEmbedLength(EmbedBuilder embedBuilder) {
    int length = 0;

    length += embedBuilder.getDescriptionBuilder().length();

    if (embedBuilder.build().getFooter() != null) {
      length += embedBuilder.build().getFooter().getText().length();
    }

    for (MessageEmbed.Field field : embedBuilder.getFields()) {
      length += field.getName().length() + field.getValue().length();
    }

    return length;
  }

  /**
   * Splits an EmbedBuilder into multiple smaller EmbedBuilders to fit within message size limits.
   *
   * @param embedBuilder The original EmbedBuilder to split.
   * @return A list of smaller EmbedBuilders.
   */
  private List<EmbedBuilder> splitEmbedBuilder(EmbedBuilder embedBuilder) {
    List<EmbedBuilder> embedChunks = new ArrayList<>();
    EmbedBuilder currentChunk = new EmbedBuilder(embedBuilder);
    currentChunk.clearFields();

    StringBuilder currentDescription = new StringBuilder(embedBuilder.getDescriptionBuilder());

    List<MessageEmbed.Field> fields = new ArrayList<>(embedBuilder.getFields());
    int currentCharCount = currentDescription.length();

    for (MessageEmbed.Field field : fields) {
      String fieldName = field.getName();
      String fieldValue = field.getValue();
      int fieldSize = fieldName.length() + fieldValue.length();

      if (currentChunk.getFields().size() >= MAX_MESSAGE_ELEMENTS
          || currentCharCount + fieldSize > MAX_CHARACTERS) {
        embedChunks.add(currentChunk);
        currentChunk = new EmbedBuilder(embedBuilder);
        currentChunk.clearFields();
        currentDescription = new StringBuilder(embedBuilder.getDescriptionBuilder());
        currentCharCount = currentDescription.length();
      }

      currentChunk.addField(fieldName, fieldValue, field.isInline());
      currentDescription.append(fieldName).append(fieldValue);
      currentCharCount += fieldSize;
    }

    if (!currentChunk.getFields().isEmpty()) {
      embedChunks.add(currentChunk);
    }

    return embedChunks;
  }

  /**
   * Sends an Embed with a Button to a text channel.
   *
   * @param textChannel The target text channel.
   * @param embed The Embed to send.
   * @param button The Button to include.
   */
  private void sendEmbedWithButton(TextChannel textChannel, MessageEmbed embed, Button button) {
    textChannel.sendMessageEmbeds(embed).setActionRows(ActionRow.of(button)).queue();
  }

  /**
   * Sends an Embed to a text channel.
   *
   * @param textChannel The target text channel.
   * @param embed The Embed to send.
   */
  private void sendEmbed(TextChannel textChannel, MessageEmbed embed) {
    textChannel.sendMessageEmbeds(embed).queue();
  }
}
