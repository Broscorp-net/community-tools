package com.community.tools.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(TraineeMentorRelationId.class)
public class TraineeMentorRelation {

  @Id
  private String gitNameMentor;
  @Id
  private String gitNameTrainee;
}
