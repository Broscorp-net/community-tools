package com.community.tools.dto;

import com.community.tools.model.TaskStatus;
import java.util.Date;
import lombok.Getter;

@Getter
public class RepositoryDto {

  private final String owner;
  private final String repositoryName;
  private final TaskStatus taskStatus;
  private final Date updated;
  private final Date created;


  public RepositoryDto(String owner, String repositoryName, TaskStatus taskStatus, Date updated,
      Date created) {
    this.owner = owner;
    this.repositoryName = repositoryName;
    this.taskStatus = taskStatus;
    this.updated = updated;
    this.created = created;
  }

}
