package com.community.tools.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task")
public class Task {

  @Id
  @GeneratedValue
  private Integer id;

  private Integer taskNumber;

  @ManyToOne
  private Estimate estimate;

  private String userId;
}
