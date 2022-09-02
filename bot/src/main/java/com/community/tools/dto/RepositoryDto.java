package com.community.tools.dto;

import com.community.tools.model.TaskStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class RepositoryDto {

  private final String owner;
  private final String repositoryName;
  private final TaskStatus taskStatus;
  private final Date updated;
  private final Date created;


}
