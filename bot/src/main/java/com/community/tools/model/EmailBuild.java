package com.community.tools.model;

import lombok.*;

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
