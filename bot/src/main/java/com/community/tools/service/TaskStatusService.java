package com.community.tools.service;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.TaskStatusRepository;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  private PullRequestsService pullRequestsService;

  @Autowired
  private TaskStatusRepository taskStatusRepository;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Autowired
  private MessageService messageService;

  /**
   * Sorted by fields.
   *
   * @param pageNumber    page number
   * @param sortByField   field for sort
   * @param sortDirection sort dir
   * @return sorting page
   */
  public Page<User> findAll(int pageNumber, String sortByField, String sortDirection) {
    Sort sort = Sort.by(sortByField);
    sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(pageNumber - 1, 100, sort);
    return stateMachineRepository.findAll(pageable);
  }

  /**
   * Create new task status.
   *
   * @param user       user
   * @param taskName   task name
   * @param taskStatus task status
   * @return new task status
   */
  public TaskStatus createTaskStatus(User user, String taskName, String taskStatus) {
    TaskStatus newTaskStatus = new TaskStatus();
    newTaskStatus.setCreated(new Date());
    newTaskStatus.setUser(user);
    newTaskStatus.setTaskName(taskName);
    newTaskStatus.setTaskStatus(taskStatus);
    taskStatusRepository.save(newTaskStatus);
    return newTaskStatus;
  }

  /**
   * Update task status.
   *
   * @param taskStatus task status
   * @param newStatus  new status
   * @return updated task status
   */
  public TaskStatus updateTaskStatus(TaskStatus taskStatus, String newStatus) {
    taskStatus.setUpdated(new Date());
    taskStatus.setTaskStatus(newStatus);
    taskStatusRepository.save(taskStatus);
    return taskStatus;
  }

  /**
   * Save into data base users task status.
   *
   * @param ghPullRequests github pull requests
   */
  public void cleanBootTasksStatus(List<GHPullRequest> ghPullRequests) {
    ghPullRequests.forEach(ghPullRequest -> {
      String gitName;
      try {
        gitName = ghPullRequest.getUser().getLogin();
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
      String title = ghPullRequest.getTitle();
      String status = pullRequestsService.getLastLabel(ghPullRequest);
      setTaskStatus(gitName, title, status);
    });
  }

  /**
   * Counts the number of completed tasks.
   *
   * @param user user
   * @return number tasks
   */
  public int countCompletedTasksByUser(User user) {
    List<TaskStatus> taskStatusList = taskStatusRepository.findAllByUser(user);
    return taskStatusList.stream().filter(taskStatus -> taskStatus.getTaskStatus().equals("done"))
        .collect(Collectors.toList()).size();
  }

  /**
   * Update task status after pull request.
   *
   * @param json json
   */
  public void updateTasksStatus(JSONObject json) {
    String gitName;
    String title;
    String status;
    try {
      gitName = json.getJSONObject("pull_request").getJSONObject("user").getString("login");
      title = json.getJSONObject("pull_request").getString("title");
      if (json.getJSONObject("pull_request").getJSONArray("labels").isEmpty()) {
        status = "pull request";
      } else {
        status = json.getJSONObject("pull_request").getJSONArray("labels")
            .getJSONObject(0).getString("name");
      }
    } catch (JSONException exception) {
      throw new RuntimeException(exception);
    }
    setTaskStatus(gitName, title, status);
  }

  /**
   * Set task status for task's user.
   *
   * @param gitName user gitName
   * @param title   title pull request
   * @param status  task status
   */
  public void setTaskStatus(String gitName, String title, String status) {
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    int distance = 3;
    User user = stateMachineRepository.findByGitName(gitName).orElse(null);
    if (!(user == null)) {
      Arrays.stream(tasksForUsers).forEach(task -> {
        if (levenshteinDistance.apply(task, title) < distance) {
          TaskStatus taskStatus = taskStatusRepository
              .findTaskStatusByUserAndTaskName(user, task).orElse(null);
          if (!(taskStatus == null)) {
            updateTaskStatus(taskStatus, status);
          } else {
            createTaskStatus(user, task, status);
          }
        }
      });
      user.setCompletedTasks(countCompletedTasksByUser(user));
      user.setDateLastActivity(LocalDate.now());
      stateMachineRepository.save(user);
    }
  }

  /**
   * Method for changing empty DB fields from null to integer 0.
   * @param userList list of users with potential nulls
   * @return ist of users without nulls
   */
  public List<User> validateNullFields(List<User> userList) {
    userList = userList.stream().peek(user -> {
      if (user.getCompletedTasks() == null) {
        user.setCompletedTasks(0);
      }
      if (user.getPointByTask() == null) {
        user.setPointByTask(0);
      }
    }).collect(Collectors.toList());
    return userList;
  }

  /**
   * This method load slack users and add slackName to the User model.
   *
   * @return List of Users.
   */
  public List<User> addPlatformNameToUser(int pageNumber,
      String sortByField, String sortDirection) {
    List<User> list = findAll(pageNumber, sortByField, sortDirection).getContent();
    Map<String, String> map = messageService.getIdWithName();
    for (User user : list) {
      String userPlatformName = map.get(user.getUserID());
      user.setPlatformName(userPlatformName);
    }

    return validateNullFields(list);
  }

  /**
   * This method load slack users and add slackName to provided user list.
   *
   * @return List of Users.
   */
  public List<User> addPlatformNameToSelectedUsers(List<User> userList) {
    Map<String, String> map = messageService.getIdWithName();
    for (User user : userList) {
      String userPlatformName = map.get(user.getUserID());
      user.setPlatformName(userPlatformName);
    }
    return validateNullFields(userList);
  }

}
