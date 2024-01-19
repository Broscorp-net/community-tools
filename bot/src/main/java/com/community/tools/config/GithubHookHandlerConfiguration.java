package com.community.tools.config;

import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.stats.UserTask;
import com.community.tools.repository.stats.UserTaskRepository;
import com.community.tools.service.TaskStatusService;
import com.community.tools.service.github.event.GithubEventHandler;
import com.community.tools.service.github.event.stats.GithubWorkflowRunEventHandler;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubHookHandlerConfiguration {

  private final GithubWorkflowRunEventHandler githubWorkflowRunEventHandler;
  private final TaskStatusService taskStatusServiceRest;
  private final UserTaskRepository userTaskRepository;

  GithubHookHandlerConfiguration(GithubWorkflowRunEventHandler githubWorkflowRunEventHandler,
      @Qualifier("taskStatusServiceRest") TaskStatusService taskStatusServiceRest,
      UserTaskRepository userTaskRepository) {
    this.githubWorkflowRunEventHandler = githubWorkflowRunEventHandler;
    this.taskStatusServiceRest = taskStatusServiceRest;
    this.userTaskRepository = userTaskRepository;
  }

  /**
   * Creates a bean with a list of webhook handlers to be invoked in no specific order upon
   * receiving an event from GitHub webhook.
   */
  @Bean("eventHandlers")
  public List<GithubEventHandler> githubHookEventHandlers() {
    List<GithubEventHandler> githubEventHandlers = new ArrayList<>();
    githubEventHandlers.add(githubWorkflowRunEventHandler);
    return githubEventHandlers;
  }

  /**
   * Creates a CommandLineRunner bean that executes at application startup and attempts to populate
   * and update user_tasks db table with relevant data proactively, without waiting for GitHub hook
   * events, exists to avoid generating empty stats tables before receiving any relevant events.
   */
  @Bean
  public CommandLineRunner statsTableInitializer() {
    return new StatsTableInitializer(taskStatusServiceRest, userTaskRepository);
  }

  @Slf4j
  public static class StatsTableInitializer implements CommandLineRunner {

    private final TaskStatusService taskStatusServiceRest;
    private final UserTaskRepository userTaskRepository;
    @Value("${defaultNumberOfDaysForStatistic}")
    private Integer defaultNumberOfDays;
    @Value("${defaultRowLimit}")
    private Integer defaultUserLimit;

    public StatsTableInitializer(TaskStatusService taskStatusServiceRest,
        UserTaskRepository userTaskRepository) {
      this.taskStatusServiceRest = taskStatusServiceRest;
      this.userTaskRepository = userTaskRepository;
    }

    @Override
    public void run(String... args) {
      log.info("Attempting to initialize user task stats using calls to GitHub REST api");
      List<UserForTaskStatusDto> userForTaskStatusDtos = taskStatusServiceRest.getTaskStatuses(
          Period.of(0, 0, defaultNumberOfDays),
          defaultUserLimit,
          Comparator.comparing(UserForTaskStatusDto::getDateLastActivity));
      userForTaskStatusDtos.forEach(
          userForTaskStatusDto -> userForTaskStatusDto.getTaskStatuses().forEach(taskStatus -> {
            UserTask userTask = new UserTask();
            userTask.setTaskName(taskStatus.getTaskName());
            userTask.setGitName(userForTaskStatusDto.getGitName());
            userTask.setTaskStatus(taskStatus.getTaskStatus());
            userTask.setLastActivity(userForTaskStatusDto.getDateLastActivity());
            userTask.setPullUrl(taskStatus.getPullUrl());
            userTaskRepository.saveAndFlush(userTask);
          }));
    }
  }
}
