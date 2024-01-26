package com.community.tools.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  //replace too discord
  private String slackId;

  public Mentors() {}

}
