package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.model.Messages;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiValidationCommand extends Command {
  private static final String OPTION_NAME = "link";
  private static final String COMMAND_NAME = "ai-validation";

  @Value("${channel.ai.validation.id}")
  private String channelId;

  @Value("${pr.manager.user.token}")
  private String userToken;

  @Value("${pr.manager.api.key}")
  private String apiKey;

  @Value("${pr.manager.api.base}")
  private String apiBaseUrl;

  @Value("${pr.validation.script.path}")
  private String scriptPath;

  @Value("${python.env}")
  private String pythonEnv;

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  public AiValidationCommand() {
    super(new CommandData(COMMAND_NAME, "Calls ai validation for your pull request"),
            new OptionData(OptionType.STRING, OPTION_NAME, "Pull request link"));
  }

  @Override
  public void run(SlashCommandEvent command) {
    log.info("Processing ai validation on pull request {}", command.getOption(OPTION_NAME));
    MessageChannel channel = command.getChannel();

    if (channel.getId().equals(channelId)) {
      Optional<OptionMapping> option = Optional.ofNullable(command.getOption(OPTION_NAME));

      if (option.isPresent()) {
        String link = option.get().getAsString();
        command.reply(Messages.VALIDATION).queue();
        submitPullRequest(link);
        log.info("Successfully submitted pull request {} for validation", link);
      } else {
        command.reply(Messages.NO_PULL_ON_VALIDATION).queue();
        log.info("Validation command failed: no link provided.");
      }
    } else {
      command.reply(Messages.WRONG_VALIDATION_CHANNEL).queue();
      log.warn("Validation command used in the wrong channel, expected ai-review, actual {}",
              channel.getName());
    }
  }

  private void submitPullRequest(String link) {
    try {
      executorService.submit(() -> validatePullRequest(link));
    } catch (RejectedExecutionException e) {
      log.error("Task {} submission rejected. Executor service might be shutting down.", link, e);
    }
  }

  private void validatePullRequest(String link) {
    try {
      link = link.trim();
      String command = String.format("%s %s %s %s %s %s", pythonEnv,
              scriptPath, link, userToken, apiKey, apiBaseUrl);
      Process process = Runtime.getRuntime().exec(command);
      getProcessStreams(process);
    } catch (IOException e) {
      throw new RuntimeException("Error while executing pull request " + link, e);
    }
  }

  private void getProcessStreams(Process process) {
    try (BufferedReader stdInput = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
         BufferedReader stdError = new BufferedReader(
                 new InputStreamReader(process.getErrorStream()))) {

      stdInput.lines().forEach(log::info);
      stdError.lines().forEach(log::error);
    } catch (IOException e) {
      throw new UncheckedIOException("Error while opening process streams", e);
    }
  }

  @EventListener(ContextClosedEvent.class)
  public void shutdownExecutor() {
    log.info("Shutting down executor service.");
    executorService.shutdown();
  }
}
