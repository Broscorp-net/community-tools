package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import java.io.IOException;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class RegisterCommand extends Command {

  private static final String OPTION_NAME = "username";
  private GitHubService gitHubService;
  private UserRepository userRepository;
  private MessageService<?> messageService;

  @Value("${newbieRole}")
  private String newbieRoleName;

  public RegisterCommand(CommandData commandData,
                         GitHubService gitHubService,
                         UserRepository userRepository,
                         @Lazy MessageService<?> messageService,
                         OptionData... options) {
    super(commandData, options);
    this.gitHubService = gitHubService;
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  /**
   * Basic constructor for the class, specifies command data and injects required beans.
   *
   * @param gitHubService checks if provided username is correct
   * @param userRepository repository for access to user's entity
   * @param messageService messaging in discord
   */
  public RegisterCommand(GitHubService gitHubService,
                            UserRepository userRepository,
                            MessageService<?> messageService) {
    super(new CommandData("register", "Saves your GitHub username"),
            new OptionData(OptionType.STRING, "username", "Your GitHub username"));
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
    String userId = command.getUser().getId();
    String username = command.getOptionsByName(OPTION_NAME).get(0).getAsString();
    try {
      gitHubService.getUserByLoginInGitHub(username);
    } catch (IOException e) {
      log.error("GitHub account with username {} was not found", username, e);
      command.reply(Messages.GITHUB_ACCOUNT_NOT_FOUND).queue();
      return;
    }
    User user = userRepository.findByUserID(userId)
        .orElseThrow(() -> new RuntimeException("User with id = [" + userId + "] was not found"));
    if (user.getGitName() == null) {
      messageService.removeRole(user.getGuildId(), userId, newbieRoleName);
      command.reply(Messages.REGISTRATION_COMPLETED).queue();
    } else {
      command.reply(Messages.USERNAME_UPDATED).queue();
    }
    user.setGitName(username);
    userRepository.save(user);
  }

}
