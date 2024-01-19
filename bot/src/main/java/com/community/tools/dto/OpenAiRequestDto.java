package com.community.tools.dto;

import java.util.Collections;
import java.util.List;

public class OpenAiRequestDto {
  private String model;
  private List<Message> messages;

  public OpenAiRequestDto(String model, String prompt) {
    this.model = model;
    this.messages = Collections.singletonList(new OpenAiRequestDto.Message("user", prompt));
  }

  public String getModel() {
    return model;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public static class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
      this.role = role;
      this.content = content;
    }

    public String getRole() {
      return role;
    }

    public String getContent() {
      return content;
    }
  }
}
