package com.community.tools.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(toBuilder = true)
public class EmailBuild {
  @NonNull
  private String userEmail;
  @NonNull
  private String subject;
  @NonNull
  private String text;
}
