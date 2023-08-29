package com.community.tools.service;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.github.ClassroomService;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskStatusService {

  private final ClassroomService classroomService;

  public TaskStatusService(ClassroomService classroomService) {
    this.classroomService = classroomService;
  }

  /**
   * Service for sorting, limiting and creating DTO.
   * @param limit - limit of users for view.
   * @param period - period of days fow view.
   * @param comparator - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  public List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<GithubUserDto> comparator) {
    log.info("running with period = {}, comparator = {}, limit = {}", period, comparator, limit);
    return classroomService.getAllActiveUsers(period)
        .stream()
        .sorted(comparator)
        .limit(limit)
        .map(githubUserDto -> new UserForTaskStatusDto(
            githubUserDto.getGitName(),
            githubUserDto.getLastCommit(),
            githubUserDto.getCompletedTasks(),
            getAllTaskNameAndStatusesForEachUser(githubUserDto)
        ))
        .collect(Collectors.toList());
  }

  /**
   * Getting List of task, and status of this task, for each user,
   *  based on labels.
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

}