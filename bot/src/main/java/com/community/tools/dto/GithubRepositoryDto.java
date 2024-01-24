package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GithubRepositoryDto(
        String repositoryName,
        String taskName,
        String lastBuildStatus,
        List<String> labels,
        int points,
        LocalDate createdAt,
        LocalDate updatedAt,
        String pullUrl) {
}
