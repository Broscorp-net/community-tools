package com.community.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIValidationResponseDTO {
    private int rating;
    private List<FileDTO> files;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDTO {
        private String filename;
        private List<CommentDTO> comments;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDTO {
        private int line;
        private String comment;
    }
}
