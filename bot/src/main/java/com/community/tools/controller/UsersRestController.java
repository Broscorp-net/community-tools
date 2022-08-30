package com.community.tools.controller;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.service.TaskStatusService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersRestController {

  private final TaskStatusService taskStatusService;
  private final LeaderBoardService leaderBoardService;

  public UsersRestController(TaskStatusService taskStatusService,
      LeaderBoardService leaderBoardService) {
    this.taskStatusService = taskStatusService;
    this.leaderBoardService = leaderBoardService;
  }

  //TODO add CRUD method or at least getAll method

  /**
   *@deprecated
   *  Unnecessary method.
   */
  @Deprecated
  @GetMapping
  @Transactional
  public List<User> getUsers(
      @RequestParam(required = false) Integer userLimit,
      @RequestParam(required = false) Integer daysFetch,
      @RequestParam(required = false) String sort) {

    Comparator<User> comparator;

    if (Objects.equals(sort, "points")) {
      comparator = Comparator.comparing(User::getTotalPoints).reversed();
    } else {
      comparator = Comparator.comparing(User::getCompletedTasks).reversed();
    }

    List<User> users;

    if (daysFetch != null) {
      users = leaderBoardService.getActiveUsersFromPeriod(daysFetch);
      users = taskStatusService.addPlatformNameToSelectedUsers(users);
    } else {
      users = taskStatusService.addPlatformNameToUser(1, "gitName", "asc");
    }

    List<User> newUsers = new ArrayList<>(users);
    newUsers.sort(comparator);

    if (userLimit != null) {
      return newUsers.subList(0, userLimit);
    } else {
      return newUsers;
    }
  }

}