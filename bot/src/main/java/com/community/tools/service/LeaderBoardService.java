package com.community.tools.service;

import com.community.tools.exception.UserNotFoundException;
import com.community.tools.dto.UserDto;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.github.ClassroomService;
import com.community.tools.util.mapper.Mapper;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaderBoardService {

  private final UserRepository userRepository;
  private final ClassroomService classroomService;
  private final Mapper<User, UserDto> mapper;

  public LeaderBoardService(UserRepository userRepository,
      ClassroomService classroomService, Mapper<User, UserDto> mapper) {
    this.userRepository = userRepository;
    this.classroomService = classroomService;
    this.mapper = mapper;
  }

  /**
   * This method get active users from period in days.
   *
   * @param period Period in days.
   * @return List of active Users.
   */
  public List<UserDto> getActiveUsersFromPeriod(Period period) {
    List<UserDto> activeUsers = new ArrayList<>();
    classroomService.getAllActiveUsers(period).forEach(githubUserDto -> {
      Optional<User> user = userRepository.findByGitName(githubUserDto.getGitName());
      user.ifPresentOrElse(user1 -> activeUsers.add(mapper.entityToDto(user1)),
          () -> log.error(new UserNotFoundException(githubUserDto.getGitName()).getMessage()));
    });
    return activeUsers;
  }

}
