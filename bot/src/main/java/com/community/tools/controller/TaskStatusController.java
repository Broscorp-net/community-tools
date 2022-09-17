package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.service.TaskStatusService;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
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

  private final TaskStatusService taskStatusService;

  public TaskStatusController(TaskStatusService taskStatusService) {
    this.taskStatusService = taskStatusService;
  }

  /**
   * Endpoint for task status service.
   * @param limit - limit of users for view.
   * @param days - period of days fow view.
   * @param sort - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  @GetMapping("/taskStatus/getStatuses")
  public ResponseEntity<List<UserForTaskStatusDto>> getTaskStatuses(
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> sort) {
    Comparator<GithubUserDto> comparator = getComparatorForTaskStatusDesc();

    if (sort.isPresent()) {
      String tmp = sort.get();
      if (tmp.equalsIgnoreCase("asc")) {
        comparator = getComparatorForTaskStatusAsc();
      } else if (tmp.equalsIgnoreCase("desc")) {
        comparator = getComparatorForTaskStatusDesc();
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    return new ResponseEntity<>(
        taskStatusService.getTaskStatuses(
            Period.ofDays(days.orElse(defaultNumberOfDays)),
            limit.orElse(defaultUserLimit),
            comparator), HttpStatus.OK);
  }

  /**
   * Endpoint for receiving task names.
   * @return - set of names.
   */
  @GetMapping("/taskStatus/getTasks")
  public ResponseEntity<Set<String>> getTaskNames() {
    return new ResponseEntity<>(
        tasksForUsers,
        HttpStatus.OK);
  }

  private static Comparator<GithubUserDto> getComparatorForTaskStatusDesc() {
    return Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed();
  }

  private static Comparator<GithubUserDto> getComparatorForTaskStatusAsc() {
    return Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed();
  }

}