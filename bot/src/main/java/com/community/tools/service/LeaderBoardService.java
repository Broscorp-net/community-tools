package com.community.tools.service;

import com.community.tools.dto.UserDto;
import com.community.tools.service.github.ClassroomService;
import java.time.Period;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LeaderBoardService {
  //todo rework this class

  private final MessageService messageService;
  private final ClassroomService classroomService;

  public LeaderBoardService(MessageService messageService, ClassroomService classroomService) {
    this.messageService = messageService;
    this.classroomService = classroomService;
  }

  /**
   * This method get active users from period in days.
   *
   * @param period Period in days.
   * @return List of Users.
   */
  public List<UserDto> getActiveUsersFromPeriod(Period period) {
    return classroomService.getAllActiveUsers(period);
  }

}
