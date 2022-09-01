package com.community.tools.service.github;

import com.community.tools.dto.GithubUserDto;
import java.time.Period;
import java.util.List;

public interface ClassroomService {

  void addUserToOrganization(String gitName);

  List<GithubUserDto> getAllActiveUsers(Period period);

}
