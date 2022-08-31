package com.community.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

//TODO task status ?

@Data
@Entity
public class TaskStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long taskStatusID;
  private String taskName;
  //TODO task status to string
  private String taskStatus;

  @ManyToOne
  @JoinColumn(name = "userid")
  @JsonIgnore
  private User user;



  @CreatedDate
  @Column(name = "created")
  private Date created;

  @LastModifiedDate
  @Column(name = "updated")
  private Date updated;


}
