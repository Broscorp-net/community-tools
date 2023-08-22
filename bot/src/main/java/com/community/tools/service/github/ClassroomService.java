package com.community.tools.service.github;

import com.community.tools.dto.GithubUserDto;

import java.io.IOException;
import java.time.Period;
import java.util.List;

public interface ClassroomService {

  void addUserToTraineesTeam(String gitName);

  List<GithubUserDto> getAllActiveUsers(Period period);

  void handleNotifications() throws IOException;

}
