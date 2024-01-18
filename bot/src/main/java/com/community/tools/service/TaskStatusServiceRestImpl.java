package com.community.tools.service;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.github.ClassroomService;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("taskStatusServiceRest")
public class TaskStatusServiceRestImpl implements TaskStatusService {

  private final ClassroomService classroomService;

  public TaskStatusServiceRestImpl(ClassroomService classroomService) {
    this.classroomService = classroomService;
  }

  /**
   * Service for sorting, limiting and creating DTO.
   *
   * @param limit      - limit of users for view.
   * @param period     - period of days fow view.
   * @param comparator - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  public List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<GithubUserDto> comparator) {
    log.info("running with period = {}, comparator = {}, limit = {}", period, comparator, limit);
    return classroomService.getAllActiveUsers(period).stream()
        .filter(this::isActiveUser)
        .sorted(comparator)
        .limit(limit)
        .map(
            githubUserDto ->
                new UserForTaskStatusDto(
                    githubUserDto.getGitName(),
                    githubUserDto.getLastCommit(),
                    githubUserDto.getCompletedTasks(),
                    getAllTaskNameAndStatusesForEachUser(githubUserDto)))
        .collect(Collectors.toList());
  }

  /**
   * Getting List of task, and status of this task, for each user, based on labels.
   *
   * @param user - user DTO.
   * @return - list of task, and status of this task, for each user.
   */
  private List<TaskNameAndStatus> getAllTaskNameAndStatusesForEachUser(GithubUserDto user) {
    return user.getRepositories().stream().map(repo -> {
      String currentStatus;
      if (repo.getLabels().size() > 1) {
        currentStatus = TaskStatus.UNDEFINED.getDescription();
      } else if (repo.getLabels().isEmpty()) {
        currentStatus = TaskStatus.NEW.getDescription();
      } else {
        currentStatus = repo.getLabels().get(0);
      }
      return new TaskNameAndStatus(repo.getTaskName(), repo.getPullUrl(),
          currentStatus);
    }).collect(Collectors.toList());
  }

  /**
   * Checks if the user is active.
   *
   * @param user The user for whom the check is performed.
   * @return true if the user is active, otherwise false.
   */
  private boolean isActiveUser(GithubUserDto user) {
    LocalDate lastCommitDate = user.getLastCommit();
    boolean allTasksApproved = areAllUserTasksApproved(user);

    if (lastCommitDate == null || daysBetween(lastCommitDate, LocalDate.now()) >= 7) {
      return false;
    }

    return allTasksApproved;
  }

  /**
   * Checks if all user tasks are approved and have reviews.
   *
   * @param user The user for whom the check is performed.
   * @return true if all tasks are approved and have reviews, otherwise false.
   */
  private boolean areAllUserTasksApproved(GithubUserDto user) {
    List<TaskNameAndStatus> taskStatuses = getAllTaskNameAndStatusesForEachUser(user);

    for (TaskNameAndStatus taskStatus : taskStatuses) {
      String status = taskStatus.getTaskStatus();
      if (status.equals(TaskStatus.UNDEFINED.getDescription())) {
        return false;
      }

      if (!hasReviews(taskStatus, user)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if a task has reviews.
   *
   * @param taskStatus The task and its status.
   * @param user       The user for whom the check is performed.
   * @return true if the task has reviews, otherwise false.
   */
  private boolean hasReviews(TaskNameAndStatus taskStatus, GithubUserDto user) {
    for (GithubRepositoryDto repository : user.getRepositories()) {
      if (isMatchingTask(repository, taskStatus)) {
        if (hasReviewLabel(repository)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if a task matches a repository.
   *
   * @param repository The repository to check.
   * @param taskStatus The task and its status.
   * @return true if the task matches the repository, otherwise false.
   */
  private boolean isMatchingTask(GithubRepositoryDto repository, TaskNameAndStatus taskStatus) {
    return repository.getTaskName().equals(taskStatus.getTaskName());
  }

  /**
   * Checks if a GitHub repository has a label containing "failure".
   *
   * @param repository The GitHub repository to check for labels.
   * @return true if the repository has a label not containing "failure", otherwise false.
   */
  private boolean hasReviewLabel(GithubRepositoryDto repository) {
    List<String> labels = repository.getLabels();
    if (labels != null && !labels.isEmpty()) {
      for (String label : labels) {
        if (!label.contains("failure")) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Calculates the number of days between two dates.
   *
   * @param startDate The start date.
   * @param endDate   The end date.
   * @return The number of days between the two dates.
   */
  private long daysBetween(LocalDate startDate, LocalDate endDate) {
    return ChronoUnit.DAYS.between(startDate, endDate);
  }

}