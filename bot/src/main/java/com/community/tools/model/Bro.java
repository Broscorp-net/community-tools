package com.community.tools.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @deprecated
 * unnecessary entity.
 */
@Deprecated
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Bro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @NonNull
  @Column
  private String loginGithub;
  @NonNull
  @Column
  private String loginSlack;
  @NonNull
  @Column
  private Date registrationDate;
}
