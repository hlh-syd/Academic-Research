package com.researchworkbench.dto.knowledge;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class KnowledgeDtos {

    private KnowledgeDtos() {
    }

    public record FolderCreateRequest(@NotBlank(message = "文件夹名称不能为空") String name) {
    }

    public record FolderRenameRequest(@NotBlank(message = "文件夹名称不能为空") String name) {
    }

    public record FolderResponse(Long id, String name, List<DocumentResponse> files) {
    }

    public record DocumentResponse(
        Long id,
        String originalName,
        String sizeLabel,
        String updatedAtLabel,
        String summary
    ) {
    }

    public record KnowledgeAskRequest(@NotBlank(message = "问题不能为空") String question, Long folderId) {
    }

    public record KnowledgeAnswerResponse(String question, String answer) {
    }

    public record KnowledgeSearchResponse(String keyword, List<DocumentResponse> documents) {
    }

    public record KnowledgeSummaryResponse(Long folderId, String summary) {
    }
}
