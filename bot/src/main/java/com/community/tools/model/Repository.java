package com.community.tools.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Repository {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String taskName;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private TaskStatus taskStatus;

  private String repositoryName;

  @Column(name = "created")
  private LocalDate created;

  @Column(name = "updated")
  private LocalDate updated;

}
