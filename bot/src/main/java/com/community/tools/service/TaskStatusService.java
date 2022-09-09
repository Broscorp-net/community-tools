package com.community.tools.service;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.service.github.ClassroomServiceImpl;
import java.time.Period;
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

  public List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<GithubUserDto> comparator) {
    log.info(
        "running..." + " period = " + period.toString() + " comparator = " + comparator.toString()
            + "limit = " + limit);
    return classroomService.getAllActiveUsers(period)
        .stream()
        .sorted(comparator)
        .limit(limit)
        .map(dto -> new UserForTaskStatusDto(
            dto.getGitName(),
            dto.getLastCommit(),
            dto.getCompletedTasks(),
            dto.getTasksAndTaskStatuses()))
        .collect(Collectors.toList());
  }

}
