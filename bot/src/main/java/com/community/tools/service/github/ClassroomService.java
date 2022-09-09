package com.community.tools.service.github;

import com.community.tools.dto.GithubUserDto;
import java.time.Period;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ClassroomService {

  void addUserToOrganization(String gitName);

  List<GithubUserDto> getAllActiveUsers(Period period,
      Pageable pageable);

}
