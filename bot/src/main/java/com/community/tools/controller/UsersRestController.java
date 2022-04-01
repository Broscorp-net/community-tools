package com.community.tools.controller;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.UserUtil;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

  @Autowired
  private UserUtil userUtil;

  /**
   * Request controller for handing api requests.
   *
   * @param userLimit query param to limit showed users
   * @param daysFetch query param to limit users by recent activity
   * @param sort      query param to sort by field
   * @return returns json with users from db according to query params
   */
  @GetMapping
  @Transactional
  public List<User> getUsers(@RequestParam(required = false) Integer userLimit,
      @RequestParam(defaultValue = "30") Integer daysFetch,
      @RequestParam(required = false) String sort) {

    List<User> users = leaderBoardService.getActiveUsersFromPeriod(daysFetch);
    users = taskStatusService.addPlatformNameToSelectedUsers(users);

    userUtil.setDateLastActivity(users);
    userUtil.setDateRegistration(users);

    List<User> newUsers = new ArrayList<>(users);
    newUsers.sort(Comparator.comparing(User::getDateRegistration).reversed());

    for (User u : newUsers) {
      if (u.getDateRegistration() != null) {
        u.setDateRegistrationFront(userUtil.convertDateToString(u.getDateRegistration()));
      }
      if (u.getDateLastActivity() != null) {
        u.setDateLastActivityFront(userUtil.convertDateToString(u.getDateLastActivity()));
      }
    }

    if (userLimit != null) {
      return newUsers.subList(0, userLimit);
    } else {
      return newUsers;
    }
  }


}