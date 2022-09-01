package com.community.tools.dto;

import lombok.Getter;

@Getter
public class RepositoryDto {

  private final String owner;

  private final String repositoryName;

  private final String taskStatus;

  public RepositoryDto(String owner, String repositoryName, String taskStatus) {
    this.owner = owner;
    this.repositoryName = repositoryName;
    this.taskStatus = taskStatus;
  }

}
