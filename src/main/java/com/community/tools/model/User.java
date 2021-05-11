package com.community.tools.model;

import javax.persistence.Entity;
import javax.persistence.Id;
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
  private byte[] stateMachine;
  private Integer karma = 0;
  private Integer pointByTask = 0;
  private String firstAnswerAboutRules;
  private String secondAnswerAboutRules;
  private String thirdAnswerAboutRules;

  @Transient
  private String slackLogin;

  /**
   * This method summ karma and pointsBy task. If fields null, return 0.
    * @return Total points
   */
  public Integer getTotalPoints() {
    return this.karma + this.pointByTask;
  }
}
