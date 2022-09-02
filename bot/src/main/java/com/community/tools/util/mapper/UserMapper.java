package com.community.tools.util.mapper;

import com.community.tools.dto.UserDto;
import com.community.tools.model.User;

public class UserMapper implements Mapper<User, UserDto> {

  @Override
  public UserDto entityToDto(User entity) {
    return new UserDto(
        entity.getUserID(),
        entity.getGitName(),
        entity.getPointByTask(),
        entity.getKarma(),
        entity.getCompletedTasks(),
        entity.getLastCommit()
    );
  }

  @Override
  public UserDto dtoToEntity(User entity) {
    throw new UnsupportedOperationException();
  }

}
