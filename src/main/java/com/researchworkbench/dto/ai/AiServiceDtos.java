package com.researchworkbench.dto.ai;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class AiServiceDtos {

    private AiServiceDtos() {
    }

    public record ToolResponse(Long id, String name, String description, String url, String tag) {
    }

    public record PromptOptimizeRequest(@NotBlank(message = "原始提示词不能为空") String draft) {
    }

    public record PromptOptimizeResponse(String optimizedPrompt) {
    }

    public record BioAssistantRequest(@NotBlank(message = "问题不能为空") String question) {
    }

    public record BioAssistantResponse(String answer) {
    }

    public record AiServiceOverviewResponse(List<ToolResponse> tools) {
    }
}
