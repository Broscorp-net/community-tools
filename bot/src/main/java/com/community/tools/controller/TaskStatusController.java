package com.community.tools.controller;

import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.service.TaskStatusService;
import java.time.Period;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskStatusController {

  @Value("${tasksForUsers}")
  private Set<String> tasksForUsers;
  @Value("${defaultNumberOfDaysForStatistic}")
  private Integer defaultNumberOfDays;
  @Value("${defaultRowLimit}")
  private Integer defaultUserLimit;
  private final Map<String, Comparator<UserForTaskStatusDto>> comparators
      = new HashMap<>();

  private final TaskStatusService taskStatusService;

  /**
   * Constructor.
   *
   * @param taskStatusService - Inject taskStatusService
   */
  public TaskStatusController(TaskStatusService taskStatusService) {
    this.taskStatusService = taskStatusService;
    comparators.put("DESC",
        Comparator.comparingInt(UserForTaskStatusDto::completedTasks).reversed());
    comparators.put("ASC",
        Comparator.comparingInt(UserForTaskStatusDto::completedTasks));
  }

  /**
   * Endpoint for task status service.
   *
   * @param limit - limit of users for view.
   * @param days  - period of days fow view.
   * @param sort  - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  @GetMapping("/taskStatus/getStatuses")
  public ResponseEntity<List<UserForTaskStatusDto>> getTaskStatuses(
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> sort) {

    Comparator<UserForTaskStatusDto> comparator = comparators.getOrDefault(
        sort.orElse("DESC").toUpperCase(),
        Comparator.comparingInt(UserForTaskStatusDto::completedTasks).reversed());

    return new ResponseEntity<>(
        taskStatusService.getTaskStatuses(
            Period.ofDays(days.orElse(defaultNumberOfDays)),
            limit.orElse(defaultUserLimit),
            comparator), HttpStatus.OK);
  }

  /**
   * Endpoint for receiving task names.
   *
   * @return - set of names.
   */
  @GetMapping("/taskStatus/getTasks")
  public ResponseEntity<Set<String>> getTaskNames() {
    return new ResponseEntity<>(
        tasksForUsers,
        HttpStatus.OK);
  }

}