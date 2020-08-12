package com.community.tools.service.github.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mentors")
public class Mentors {

  @Id
  private String gitNick;
  private String slackId;

  public Mentors() {
  }

  public Mentors(String gitNick, String slackId) {
    this.gitNick = gitNick;
    this.slackId = slackId;
  }

  public void setGitNick(String gitNick) {
    this.gitNick = gitNick;
  }

  public void setSlackId(String slackId) {
    this.slackId = slackId;
  }

  public String getGitNick() {
    return gitNick;
  }

  public String getSlackId() {
    return slackId;
  }
}
