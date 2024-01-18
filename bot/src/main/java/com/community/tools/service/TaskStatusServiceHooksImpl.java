package com.community.tools.service;

import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.stats.UserTask;
import com.community.tools.repository.stats.UserTaskRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service("taskStatusServiceHooks")
@Profile("discord")
public class TaskStatusServiceHooksImpl implements TaskStatusService {

  private final UserTaskRepository userTaskRepository;

  public TaskStatusServiceHooksImpl(UserTaskRepository userTaskRepository) {
    this.userTaskRepository = userTaskRepository;
  }

  @Override
  public List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<UserForTaskStatusDto> comparator) {
    LocalDate startDate = LocalDate.ofEpochDay(LocalDate
        .now()
        .minus(period).toEpochDay());
    return userTaskRepository
        .findUserTasksByLastActivityAfter(startDate)
        .stream()
        .collect(Collectors.groupingBy(UserTask::getGitName))
        .entrySet()
        .stream()
        .map(entry -> {
          List<TaskNameAndStatus> taskNamesAndStatuses = new ArrayList<>();
          entry.getValue().forEach(
              task -> taskNamesAndStatuses.add(
                  new TaskNameAndStatus(task.getTaskName(), task.getPullUrl(),
                      task.getTaskStatus())));

          final int completedTasksCount = (int) taskNamesAndStatuses.stream()
              .filter(it -> it.getTaskStatus().equals(
                  TaskStatus.DONE.getDescription())).count();

          /* This is supposed to throw NoSuchElementException
          in case it cannot find lastActivity for gitName
          because it is PPK in db, therefore at least one such record MUST exist,
          if it does not, something went terribly wrong,
          there is no point in trying to recover.
           */
          final LocalDate lastActive = entry.getValue().stream().max(
              Comparator.comparing(UserTask::getLastActivity)).get().getLastActivity();
          return new UserForTaskStatusDto(entry.getKey(),
              lastActive, completedTasksCount, taskNamesAndStatuses);
        })
        .sorted(comparator)
        .limit(limit)
        .collect(Collectors.toList());
  }
}