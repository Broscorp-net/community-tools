package com.community.tools.service;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.ClassroomServiceImpl;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

  private final ClassroomServiceImpl classroomService;

  public TaskStatusService(ClassroomServiceImpl classroomService) {
    this.classroomService = classroomService;
  }

  public List<GithubUserDto> getAll(Period period) {
    return classroomService.getAllActiveUsers(period);
  }

  public List<GithubRepositoryDto> getTaskStatusesForName(String taskName, Period period) {
    List<GithubRepositoryDto> result = new ArrayList<>();
    classroomService.getAllActiveUsers(period).forEach(
        githubUserDto -> githubUserDto.getRepositories().forEach(githubRepositoryDto -> {
          if (githubRepositoryDto.getTaskName().equals(taskName)) {
            result.add(githubRepositoryDto);
          }
        })
    );
    return result;
  }

}
