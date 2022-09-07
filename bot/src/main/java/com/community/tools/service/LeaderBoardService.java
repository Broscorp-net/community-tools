package com.community.tools.service;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForLeaderboardDto;
import com.community.tools.service.github.ClassroomService;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
  public List<UserForLeaderboardDto> getLeaderBoard(Period period,
      Comparator<GithubUserDto> comparator) {
    return classroomService.getAllActiveUsers(period)
        .stream()
        .sorted(comparator)
        .map(dto -> new UserForLeaderboardDto(
            dto.getGitName(),
            dto.getLastCommit(),
            dto.getCompletedTasks(),
            dto.getTotalPoints()
        )).collect(Collectors.toList());
  }


}