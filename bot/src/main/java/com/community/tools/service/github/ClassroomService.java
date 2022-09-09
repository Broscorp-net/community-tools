package com.community.tools.service.github;

import com.community.tools.dto.GithubUserDto;
import java.time.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassroomService {

  void addUserToOrganization(String gitName);

  Page<GithubUserDto> getAllActiveUsers(Period period, Pageable pageable);
}
