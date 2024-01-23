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
@IdClass(StudentMentorRelationId.class)
public class StudentMentorRelation {

  @Id
  private String gitNameMentor;
  @Id
  private String gitNameStudent;
}
