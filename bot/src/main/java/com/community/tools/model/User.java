package com.community.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  @Column(name = "userid")
  private String userId;
  private String guildId;
  private String gitName;
  private LocalDate dateRegistration;
  private LocalDate dateLastActivity;
  @OneToMany
  private Set<Mentors> mentors;
  @JsonIgnore
  private byte[] stateMachine;
  private Integer karma = 0;
  private Integer pointByTask = 0;

  @Transient
  private String platformName;

  private String email;

  private Integer completedTasks;

  private Date lastCommit;

}