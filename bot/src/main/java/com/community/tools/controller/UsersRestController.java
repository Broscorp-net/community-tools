package com.community.tools.controller;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.service.TaskStatusService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersRestController {

  @Autowired
  private TaskStatusService taskStatusService;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  LeaderBoardService leaderBoardService;

  /**
   * Request controller for handing api requests.
   *
   * @param userLimit query param to limit showed users
   * @param daysFetch query param to limit users by recent activity
   * @return returns json with users from db according to query params
   */
  @GetMapping
  @Transactional
  public List<User> getUsers(@RequestParam(required = false) Integer userLimit,
      @RequestParam(required = false) Integer daysFetch) {

    List<User> users;

    if (daysFetch != null) {
      users = leaderBoardService.getActiveUsersFromPeriod(daysFetch);
      users = taskStatusService.addPlatformNameToSelectedUsers(users);
    } else {
      users = taskStatusService.addPlatformNameToUser(1, "gitName", "asc");
    }

    List<User> newUsers = new ArrayList<>(users);

    if (userLimit != null) {
      return newUsers.subList(0, userLimit);
    } else {
      return newUsers;
    }
  }


}