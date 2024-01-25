package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Command(name = "register", description = "Saves your GitHub username",
    options = "username", optionTypes = OptionType.STRING,
    optionsDescriptions = "Your GitHub username", optionsRequirements = true)
@RequiredArgsConstructor
public class RegisterCommand {

  private static final String OPTION_NAME = "username";
  private final GitHubService gitHubService;
  private final UserRepository userRepository;
  private final MessageService<?> messageService;

  @Value("${newbieRole}")
  private String newbieRoleName;

  /**
   * Saves user's GitHub username to database and removes newbie role.
   * @param command received event from Discord
   */
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
