package com.researchworkbench.dto.research;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class ResearchDtos {

    private ResearchDtos() {
    }

    public record SearchRequest(@NotBlank(message = "关键词不能为空") String keyword) {
    }

    public record PaperResponse(
        Long id,
        String title,
        String authors,
        String publicationYear,
        String journal,
        String summary,
        List<String> tags,
        Integer citedBy,
        boolean inKnowledgeBase
    ) {
    }

    public record HistoryResponse(Long id, String keyword, String timeLabel, Integer hits) {
    }

    public record MindMapNodeResponse(Long id, String title, String detail, boolean accent) {
    }

    public record QuickSummaryResponse(Long id, String label, String content, String tone) {
    }

    public record ResearchOverviewResponse(
        List<PaperResponse> papers,
        List<HistoryResponse> histories,
        List<MindMapNodeResponse> mindMapNodes,
        List<QuickSummaryResponse> quickSummaries
    ) {
    }
}
