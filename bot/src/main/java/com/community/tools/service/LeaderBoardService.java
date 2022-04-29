package com.community.tools.service;

import com.community.tools.model.ServiceUser;
import com.community.tools.model.User;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaderBoardService {

  @Autowired
  StateMachineRepository stateMachineRepository;

  @Autowired
  private MessageService messageService;

  @Autowired
  private GitHubService gitHubService;


  /**
   * This method load users and add Name to the User model.
   *
   * @return List of Users.
   */
  public List<User> addNameToUser() {
    List<User> list = stateMachineRepository.findAll();
    Set<ServiceUser> users = messageService.getAllUsers();
    Map<String, String> map = users.stream()
        .filter(u -> u.getName() != null)
        .collect(Collectors.toMap(ServiceUser::getId, ServiceUser::getName));
    for (User user : list) {
      String name = map.get(user.getUserID());
      user.setPlatformName(name);
    }
    return list;
  }

  /**
   * This method get active users from period in days and add their to the User model.
   *
   * @param days Period in days.
   * @return List of Users.
   */
  public List<User> getActiveUsersFromPeriod(int days) {
    LocalDate tempDate = LocalDate.now().minusDays(days);
    Date date = Date.from(tempDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    List<User> list = addNameToUser();
    Set<String> userNames = gitHubService.getActiveUsersFromGit(date);
    return list.stream()
        .filter(user -> userNames
            .contains(user.getGitName())).collect(Collectors.toList());
  }

  /**
   * This method checks whether the user is active from period.
   *
   * @param u User.
   * @param dateInPast Period in days.
   * @return User is active from period.
   */
  public boolean isActiveFromPeriod(User u, LocalDate dateInPast) {
    if (u.getDateLastActivity() == null) {
      return false;
    } else {
      return dateInPast.isBefore(u.getDateLastActivity());
    }
  }

}
