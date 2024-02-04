package com.community.tools.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiValidationResponseDto {
  private int rating;
  private List<FileDto> files;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FileDto {
    private String filename;
    private List<CommentDto> comments;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CommentDto {
    private int line;
    private String comment;
  }
}
