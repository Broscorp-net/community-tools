package com.community.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  private String userId;
  private String gitName;
  private LocalDate dateRegistration;
  private LocalDate dateLastActivity;
  @JsonIgnore
  private byte[] stateMachine;
  private Integer karma = 0;
  private Integer pointByTask = 0;
  private String firstAnswerAboutRules;
  private String secondAnswerAboutRules;
  private String thirdAnswerAboutRules;

  @Transient
  private String platformName;

  private String email;

  private Integer completedTasks;

  private Date lastCommit;

}
