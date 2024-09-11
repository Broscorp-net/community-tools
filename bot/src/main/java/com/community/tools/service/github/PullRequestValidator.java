package com.community.tools.service.github;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@Service
public class PullRequestValidator {
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

    private final Queue<String> pulls = new ArrayDeque<>();

    public void validatePR(GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        if (channel.getId().equals(channelId)) {
            String message = event.getMessage().getContentRaw();

            pulls.add(message);
        }
    }

    @Scheduled(fixedDelay = 2000)
    private void checkPulls() {
        if (!pulls.isEmpty()) {
            String pull = pulls.poll();
            validatePullRequest(pull);
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
            throw new RuntimeException("Error while opening process streams", e);
        }
    }
}
