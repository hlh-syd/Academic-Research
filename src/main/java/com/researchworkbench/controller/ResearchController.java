package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.research.ResearchDtos;
import com.researchworkbench.service.ResearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/research")
public class ResearchController {

    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @GetMapping("/overview")
    public ApiResponse<ResearchDtos.ResearchOverviewResponse> overview(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.ok(researchService.getOverview(keyword));
    }

    @PostMapping("/papers/{paperId}/knowledge-folders/{folderId}")
    public ApiResponse<ResearchDtos.PaperResponse> collectToKnowledgeBase(
        @PathVariable Long paperId,
        @PathVariable Long folderId
    ) {
        return ApiResponse.ok(researchService.collectToKnowledgeBase(paperId, folderId));
    }
}
