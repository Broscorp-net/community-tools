package com.community.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;


@Data
@Entity
@Table(name = "state_entity")
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

  @OneToMany(mappedBy = "user")
  private List<TaskStatus> taskStatuses;

  /**
   * This method summ karma and pointsBy task. If fields null, return 0.
   *
   * @return Total points
   */
  public Integer getTotalPoints() {
    return this.karma + this.pointByTask;
  }

}
