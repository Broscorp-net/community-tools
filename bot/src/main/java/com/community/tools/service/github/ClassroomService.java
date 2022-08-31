package com.community.tools.service.github;

import com.community.tools.dto.UserDto;
import java.time.Period;
import java.util.List;

public interface ClassroomService {

  void addUserToOrganization(UserDto userDto);

  List<UserDto> getAllActiveUsers(Period period);

}
