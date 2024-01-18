package com.community.tools.config;

import com.community.tools.service.github.event.GithubEventHandler;
import com.community.tools.service.github.event.stats.GithubWorkflowRunEventHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("discord")
public class GithubHookHandlerConfiguration {

  private final GithubWorkflowRunEventHandler githubWorkflowRunEventHandler;

  /**
   * Creates a bean with a list of webhook handlers to be invoked in no specific order upon
   * receiving an event from GitHub webhook.
   */
  @Bean("eventHandlers")
  @Profile("discord")
  public List<GithubEventHandler> githubHookEventHandlers() {
    List<GithubEventHandler> githubEventHandlers = new ArrayList<>();
    githubEventHandlers.add(githubWorkflowRunEventHandler);
    return githubEventHandlers;
  }
}
