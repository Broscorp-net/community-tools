package com.community.tools.service;

import com.community.tools.discord.Command;
import com.community.tools.discord.DiscordService;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.community.tools.service.github.PullRequestValidator;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener implements EventListener {
  private UserRepository userRepository;
  private DiscordService messageService;
  private List<Command> commands;
  private PullRequestValidator prValidator;

  public MessageListener(UserRepository userRepository,
                         @Lazy DiscordService messageService,
                         List<Command> commands,
                         PullRequestValidator prValidator) {
      this.userRepository = userRepository;
      this.messageService = messageService;
      this.commands = commands;
      this.prValidator = prValidator;
  }

  @Override
  public void memberJoin(GuildMemberJoinEvent event) {
    log.info("User joined guild: {}", event.getMember().getUser().getName());
  }

  @Override
  public void commandReceived(SlashCommandEvent event) {
    commands.stream()
            .filter(c -> c.getCommandData().getName().equals(event.getName()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("No matching command found for event = ["
                    + event.getName() + "]"))
            .run(event);
  }

  /**
   * Receives and processes messages in guilds.
   * Currently, there is no reaction from bot, as sending default message for any event
   * leads to spamming.
   *
   * @param event received event from Discord
   */
  @Override
  public void guildMessageReceived(GuildMessageReceivedEvent event) {
      log.info("Received message from guild: " + event.getMessage().getContentRaw());
      prValidator.validatePR(event);
  }

  @Override
  public void privateMessageReceived(PrivateMessageReceivedEvent event) {
    messageService.sendPrivateMessage(event.getAuthor().getName(),
            Messages.DEFAULT_MESSAGE);
  }

  /**
   * Resets user's entity.
   *
   * @param userId user's id
   * @param guildId guild, the user has joined
   * @return true if user is new, false if he is registered
   */
  private boolean resetUser(String userId, String guildId) {
    Optional<User> userOptional = userRepository.findByUserId(userId);
    User user = userOptional.orElseGet(User::new);
    user.setUserId(userId);
    user.setGuildId(guildId);
    user.setDateRegistration(LocalDate.now());
    userRepository.save(user);
    return user.getGitName() == null;
  }

}
