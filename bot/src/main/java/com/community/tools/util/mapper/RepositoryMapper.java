package com.community.tools.util.mapper;

import com.community.tools.dto.RepositoryDto;
import com.community.tools.model.Repository;

public class RepositoryMapper implements Mapper<Repository, RepositoryDto> {

  @Override
  public RepositoryDto entityToDto(Repository entity) {
    return new RepositoryDto(
        entity.getUser().getGitName(),
        entity.getRepositoryName(),
        entity.getTaskStatus(),
        entity.getUpdated(),
        entity.getCreated()
    );
  }

  @Override
  public RepositoryDto dtoToEntity(Repository entity) {
    throw new UnsupportedOperationException();
  }

}
