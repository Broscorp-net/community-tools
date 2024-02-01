package com.community.tools.model.status;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_tasks")
@IdClass(UserTaskId.class)
@Getter
@Setter
public class UserTask {

  @Id
  private String gitName;
  @Id
  private String taskName;
  private LocalDate lastActivity;
  private String pullUrl;
  private String taskStatus;
  private String headCommitId;
}
