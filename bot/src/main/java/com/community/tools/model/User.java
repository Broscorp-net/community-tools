package com.community.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  private String userID;
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
