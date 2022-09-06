package com.community.tools.service;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.ClassroomServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

  private final ClassroomServiceImpl classroomService;

  public TaskStatusService(ClassroomServiceImpl classroomService) {
    this.classroomService = classroomService;
  }

  public List<GithubUserDto> getTasksForUsers(String taskName) {
    return null;
  }

}
