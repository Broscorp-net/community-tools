package com.community.tools.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class TraineeMentorRelationId implements Serializable {

  private String gitNameMentor;
  private String gitNameTrainee;
}
