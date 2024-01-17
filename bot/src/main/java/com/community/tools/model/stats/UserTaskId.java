package com.community.tools.model.stats;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class UserTaskId implements Serializable {

  private String gitName;
  private String taskName;
}
