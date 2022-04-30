package com.community.tools.controller;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.service.TaskStatusService;
import java.time.LocalDate;
import java.util.Comparator;
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

  private static final int daysFetch = 30;

  /**
   * Request controller for handing api requests.
   *
   * @param userLimit query param to limit showed users
   * @param sort      query param to sort by field
   * @return returns json with users from db according to query params
   */
  @GetMapping
  @Transactional
  public List<User> getUsers(@RequestParam(required = false) Integer userLimit,
      @RequestParam(required = false) String sort) {

    Comparator<User> comparator;

    if (Objects.equals(sort, "points")) {
      comparator = Comparator.comparing(User::getTotalPoints).reversed();
    } else {
      comparator = Comparator.comparing(User::getCompletedTasks).reversed();
    }

    List<User> users = taskStatusService.addPlatformNameToUser(
        1, "gitName", "asc");

    LocalDate dateInPast = LocalDate.now().minusDays(daysFetch);

    List<User> newUsers = users.stream()
        .filter(u -> leaderBoardService.isActiveFromPeriod(u, dateInPast))
        .sorted(comparator)
        .collect(Collectors.toList());

    if (userLimit != null) {
      return newUsers.subList(0, userLimit);
    } else {
      return newUsers;
    }
  }

}