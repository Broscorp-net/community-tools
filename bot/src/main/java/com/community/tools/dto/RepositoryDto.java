package com.community.tools.dto;

import com.community.tools.model.TaskStatus;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Deprecated
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class RepositoryDto {

  private final String owner;
  private final String repositoryName;
  private final TaskStatus taskStatus;
  private final LocalDate updated;
  private final LocalDate created;


}
