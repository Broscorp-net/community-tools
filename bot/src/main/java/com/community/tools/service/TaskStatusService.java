package com.community.tools.service;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.ClassroomServiceImpl;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskStatusService {

  private final ClassroomServiceImpl classroomService;

  public TaskStatusService(ClassroomServiceImpl classroomService) {
    this.classroomService = classroomService;
  }

  //TODO add comparator interface to method's arguments
  public List<GithubUserDto> getAll(Period period) {
    log.info("running...");
    return classroomService.getAllActiveUsers(period)
        .stream()
        .sorted(Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed())
        .collect(Collectors.toList());
  }

  /**
   * @deprecated unused
   * @param taskName
   * @param period
   * @return
   */
  @Deprecated
  public List<GithubRepositoryDto> getTaskStatusesForName(String taskName, Period period) {
    List<GithubRepositoryDto> result = new ArrayList<>();
    classroomService.getAllActiveUsers(period).forEach(
        githubUserDto -> githubUserDto.getRepositoryWithName(taskName).ifPresent(result::add));
    return result.stream()
        .sorted(Comparator.comparingInt(GithubRepositoryDto::getPoints).reversed())
        .collect(Collectors.toList());
  }

}
