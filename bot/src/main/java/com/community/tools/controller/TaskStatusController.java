package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.TaskStatusService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskStatusController {

  @Value("${tasksForUsers}")
  private Set<String> tasksForUsers;

  private final TaskStatusService taskStatusService;

  public TaskStatusController(TaskStatusService taskStatusService) {
    this.taskStatusService = taskStatusService;
  }

  @GetMapping("/taskStatus/getTaskForName/{taskName}")
  public ResponseEntity<List<GithubUserDto>> getUsersForTask(@PathVariable String taskName) {

    if (tasksForUsers.contains(taskName)) {
      return new ResponseEntity<>(taskStatusService.getTasksForUsers(taskName),
          HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

  }

}