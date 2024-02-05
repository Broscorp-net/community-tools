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
  private double temperature;

  /**
   * Constructor for DTO.
   *
   * @param model model that is intended to be used
   * @param prompt prompt to work with
   * @param temperature ai temperature
   */
  public OpenAiRequestDto(String model, String prompt, double temperature) {
    this.model = model;
    this.messages = Collections.singletonList(new Message("user", prompt));
    this.temperature = temperature;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }
}