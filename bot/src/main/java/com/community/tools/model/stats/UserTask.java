package com.community.tools.model.stats;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
}
