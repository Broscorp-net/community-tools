package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterCommand extends Command {

  private static final String OPTION_NAME = "username";
  private final GitHubService gitHubService;
  private final UserRepository userRepository;
  private final MessageService<?> messageService;

  @Value("${newbieRole}")
  private String newbieRoleName;

  @Value("${guild.id}")
  private String guildId;

  /**
   * Basic constructor for the class, specifies command data and injects required beans.
   *
   * @param gitHubService  checks if provided username is correct
   * @param userRepository repository for access to user's entity
   * @param messageService messaging in discord
   */
  public RegisterCommand(GitHubService gitHubService,
      UserRepository userRepository,
      @Lazy MessageService<?> messageService) {
    super(new CommandData("register", "Saves your GitHub username"),
        new OptionData(OptionType.STRING, OPTION_NAME, "Your GitHub username"));
    this.gitHubService = gitHubService;
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  /**
   * Saves user's GitHub username to database and removes newbie role.
   *
   * @param command received event from Discord
   */
  @Override
  public void run(SlashCommandEvent command) {
    log.info("Processing register command.");
    String userId = command.getUser().getId();
    User user = getUser(userId);
    Optional<OptionMapping> option = Optional.ofNullable(command.getOption(OPTION_NAME));

    if (option.isEmpty()) {
      handleNoOption(command, user.getGitName());
      return;
    }

    String username = option.get().getAsString();
    if (!gitHubUserExists(username)) {
      log.info("No GitHub account under username " + username + " was found.");
      command.reply(Messages.GITHUB_ACCOUNT_NOT_FOUND).queue();
      return;
    }

    updateUser(user, username);
    command.reply(Messages.REGISTRATION_COMPLETED).queue();
  }

  private User getUser(String userId) {
    return userRepository.findByUserId(userId)
        .orElseGet(() -> createNewUser(userId));
  }

  private User createNewUser(String userId) {
    messageService.addRoleToUser(guildId, userId, newbieRoleName);
    User user = new User();
    user.setUserId(userId);
    user.setGuildId(guildId);
    user.setDateRegistration(LocalDate.now());
    return user;
  }

  private boolean gitHubUserExists(String username) {
    try {
      gitHubService.getUserByLoginInGitHub(username);
      return true;
    } catch (IOException e) {
      log.error("GitHub account with username {} was not found", username, e);
      return false;
    }
  }

  private void updateUser(User user, String username) {
    if (user.getGitName() == null) {
      messageService.removeRole(user.getGuildId(), user.getUserId(), newbieRoleName);
    }
    user.setGitName(username);
    userRepository.save(user);
  }

  private void sendReply(SlashCommandEvent command, String gitName) {
    if (gitName == null) {
      command.reply(Messages.REGISTRATION_COMPLETED).queue();
    } else {
      command.reply(Messages.USERNAME_UPDATED).queue();
    }
  }

  private static void handleNoOption(SlashCommandEvent command, String gitName) {
    if (gitName == null) {
      command.reply(Messages.NOT_REGISTERED).queue();
    } else {
      command.reply(String.format(Messages.CURRENT_USERNAME, gitName)).queue();
    }
  }


}
