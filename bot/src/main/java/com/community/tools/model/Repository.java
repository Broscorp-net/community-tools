package com.community.tools.model;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Entity
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
  private Date created;

  @Column(name = "updated")
  private Date updated;

}
