package com.community.tools.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
public class TaskStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long taskStatusID;
  private String taskName;
  private String taskStatus;

  @ManyToOne
  @JoinColumn(name = "userid")
  private User user;



  @CreatedDate
  @Column(name = "created")
  private Date created;

  @LastModifiedDate
  @Column(name = "updated")
  private Date updated;


}
