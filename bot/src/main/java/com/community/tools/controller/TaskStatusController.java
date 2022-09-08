package com.community.tools.controller;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.TaskStatusService;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskStatusController {

  @Value("${tasksForUsers}")
  private Set<String> tasksForUsers;
  @Value("${defaultNumberOfDaysForStatistic}")
  private Integer defaultNumberOfDays;

  private final TaskStatusService taskStatusService;

  public TaskStatusController(TaskStatusService taskStatusService) {
    this.taskStatusService = taskStatusService;
  }

  @GetMapping("/taskStatus/getAll")
  public ResponseEntity<List<GithubUserDto>> getAllTaskStatuses() {
    return new ResponseEntity<>(
        taskStatusService.getAll(Period.ofDays(defaultNumberOfDays)),
        HttpStatus.OK);
  }

  /**
   * @deprecated Unused endpoint
   */
  @Deprecated
  @GetMapping("/taskStatus/getTaskForName/{taskName}")
  public ResponseEntity<List<GithubRepositoryDto>> getTaskStatusesForName(@PathVariable String taskName,
      @RequestParam(required = false) Optional<Integer> days) {

    if (tasksForUsers.contains(taskName)) {
      return new ResponseEntity<>(
          taskStatusService.getTaskStatusesForName(taskName,
              Period.ofDays(days.orElse(defaultNumberOfDays))),
          HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

  }

}