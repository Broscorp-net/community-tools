package com.community.tools.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Builder
public class EmailBuild {
  @NonNull
  private String userEmail;
  @NonNull
  private String subject;
  @NonNull
  private String text;
}
