package com.community.tools.dto;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenAiRequestDto {
  private String model;
  private List<Message> messages;

  public OpenAiRequestDto(String model, String prompt) {
    this.model = model;
    this.messages = Collections.singletonList(new Message("user", prompt));
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }
}
