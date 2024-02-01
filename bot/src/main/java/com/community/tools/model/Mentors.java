package com.community.tools.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mentors")
public class Mentors {

  @Id
  private String gitNick;
  @Column(unique = true)
  private String discordName;
  //replace too discord
  private String slackId;

}
