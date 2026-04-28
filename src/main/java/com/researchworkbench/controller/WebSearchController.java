package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.web.WebSearchDtos;
import com.researchworkbench.service.WebSearchService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web-search")
public class WebSearchController {

    private final WebSearchService webSearchService;

    public WebSearchController(WebSearchService webSearchService) {
        this.webSearchService = webSearchService;
    }

    @PostMapping("/execute")
    public ApiResponse<WebSearchDtos.SearchResponse> execute(@Valid @RequestBody WebSearchDtos.ExecuteRequest request) {
        return ApiResponse.ok(webSearchService.execute(request));
    }

    @GetMapping("/history")
    public ApiResponse<List<WebSearchDtos.SearchResponse>> history() {
        return ApiResponse.ok(webSearchService.getHistory());
    }

    @PostMapping("/history/{historyId}/save")
    public ApiResponse<WebSearchDtos.SearchResponse> save(@PathVariable Long historyId) {
        return ApiResponse.ok(webSearchService.save(historyId));
    }
}
