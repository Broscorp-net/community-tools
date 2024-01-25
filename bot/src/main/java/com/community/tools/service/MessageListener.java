package com.community.tools.service;

import com.community.tools.discord.CommandHandler;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener implements EventListener {

  private final UserRepository userRepository;
  private final MessageService<?> messageService;
  private final CommandHandler commandHandler;

  @Value("${welcomeChannel}")
  private String welcomeChannelName;

  @Value("${newbieRole}")
  private String newbieRoleName;

  @Override
  public void memberJoin(GuildMemberJoinEvent event) {
    String userId = event.getUser().getId();
    String guildId = event.getGuild().getId();
    resetUser(userId, guildId);
    messageService.addRoleToUser(guildId, userId, newbieRoleName);
    messageService.sendMessageToConversation(welcomeChannelName,
        String.format(Messages.WELCOME_MENTION, event.getUser().getAsMention()));
  }

  @Override
  public void commandReceived(SlashCommandEvent event) {
    commandHandler.runCommand(event);
  }

  @Override
  public void guildMessageReceived(GuildMessageReceivedEvent event) {
    if (event.getMessage().getType() != MessageType.GUILD_MEMBER_JOIN) {
      messageService.sendMessageToConversation(event.getChannel().getName(),
          Messages.DEFAULT_MESSAGE);
    }
  }

  @Override
  public void privateMessageReceived(PrivateMessageReceivedEvent event) {
    messageService.sendPrivateMessage(event.getAuthor().getName(),
        Messages.DEFAULT_MESSAGE);
  }

  private void resetUser(String userId, String guildId) {
    User stateEntity = new User();
    stateEntity.setUserID(userId);
    stateEntity.setGuildId(guildId);
    stateEntity.setDateRegistration(LocalDate.now());
    userRepository.save(stateEntity);
  }

}
