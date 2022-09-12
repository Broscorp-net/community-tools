package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.service.TaskStatusService;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

  @GetMapping("/taskStatus/getStatuses")
  //TODO delete annotation
  @CrossOrigin(origins = "http://localhost:4200")
  public ResponseEntity<List<UserForTaskStatusDto>> getTaskStatuses(
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> sort) {
    Comparator<GithubUserDto> comparator = getComparatorForTaskStatusDESC();

    if (sort.isPresent()) {
      String tmp = sort.get();
      if (tmp.equalsIgnoreCase("asc")) {
        comparator = getComparatorForTaskStatusASC();
      } else if (tmp.equalsIgnoreCase("desc")) {
        comparator = getComparatorForTaskStatusDESC();
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

  @GetMapping("/taskStatus/getTasks")
  //TODO delete annotation
  @CrossOrigin(origins = "http://localhost:4200")
  public ResponseEntity<Set<String>> getTaskNames() {
    return new ResponseEntity<>(
        tasksForUsers,
        HttpStatus.OK);
  }

  private static Comparator<GithubUserDto> getComparatorForTaskStatusDESC() {
    return Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed();
  }

  private static Comparator<GithubUserDto> getComparatorForTaskStatusASC() {
    return Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed();
  }

}