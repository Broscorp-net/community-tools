package com.community.tools.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Getter
@Setter
@Entity
public class TaskStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long taskStatusID;
  private String taskName;

  //TODO task status to string
  private String taskStatus;


  @OneToOne(mappedBy = "taskStatus")
  private Repository repository;

  @CreatedDate
  @Column(name = "created")
  private Date created;

  @LastModifiedDate
  @Column(name = "updated")
  private Date updated;


}
