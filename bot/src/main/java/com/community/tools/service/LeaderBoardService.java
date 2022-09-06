package com.community.tools.service;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.ClassroomService;
import java.time.Period;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaderBoardService {


  private final ClassroomService classroomService;

  public LeaderBoardService(ClassroomService classroomService) {
    this.classroomService = classroomService;
  }

  /**
   * This method get active users from period in days.
   *
   * @param period Period in days.
   * @return List of active Users.
   */
  public List<GithubUserDto> getActiveUsersFromPeriod(Period period) {
    return classroomService.getAllActiveUsers(period);
  }

  //TODO delete it later
  public List<GithubUserDto> test(Period period) {
    return classroomService.getAllActiveUsers(period);
  }

}
