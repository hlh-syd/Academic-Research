package com.researchworkbench.dto.web;

import jakarta.validation.constraints.NotBlank;

public final class WebSearchDtos {

    private WebSearchDtos() {
    }

    public record ExecuteRequest(
        @NotBlank(message = "搜索来源不能为空") String source,
        @NotBlank(message = "搜索词不能为空") String query
    ) {
    }

    public record SearchResponse(Long id, String source, String query, String excerpt, String markdownContent, boolean saved) {
    }
}
