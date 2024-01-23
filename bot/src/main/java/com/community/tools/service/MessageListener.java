package com.community.tools.service;

import com.community.tools.discord.Command;
import com.community.tools.model.Messages;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener implements EventListener {

  private final TrackingService trackingService;
  private final MessageService<?> messageService;
  private final List<Command> commands;

  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  @Value("${welcomeChannel}")
  private String welcomeChannelName;

  @Value("${newbieRole}")
  private String newbieRoleName;

  @Override
  public void memberJoin(GuildMemberJoinEvent event) {
    User user = event.getUser();
    String userId = user.getId();
    String guildId = event.getGuild().getId();
    messageService.addRoleToUser(guildId, userId, newbieRoleName);
    messageService.sendMessageToConversation(welcomeChannelName,
        String.format(Messages.WELCOME_MENTION, user.getAsMention()));
    try {
      trackingService.resetUser(userId, guildId);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void commandReceived(SlashCommandEvent event) {
    commands.stream()
        .filter(c -> c.getCommandData().getName().equals(event.getName()))
        .forEach(c -> c.run(event));
  }

  @Override
  public void guildMessageReceived(GuildMessageReceivedEvent event) {
    messageReceived(event.getMessage());
  }

  @Override
  public void privateMessageReceived(PrivateMessageReceivedEvent event) {
    messageReceived(event.getMessage());
  }

  private void messageReceived(Message message) {
    String guildId = message.getGuild().getId();
    String userId = message.getAuthor().getId();

    try {
      if (message.getContentRaw().equalsIgnoreCase("reset")
          && testModeSwitcher) {
        trackingService.resetUser(userId, guildId);
      } else {
        trackingService.doAction(message);
      }
    } catch (Exception exception) {
      throw new RuntimeException("Impossible to answer request with id = "
        + userId, exception);
    }
  }

}
