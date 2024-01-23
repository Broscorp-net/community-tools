package com.community.tools.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "mentors")
public class Mentors {

  @Id
  private String gitNick;
  @Column(unique = true)
  private String discordName;
  private String slackId;

  public Mentors() {
  }

}
