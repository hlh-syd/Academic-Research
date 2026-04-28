package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.ai.AiServiceDtos;
import com.researchworkbench.service.AiServiceFacade;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-services")
public class AiServiceController {

    private final AiServiceFacade aiServiceFacade;

    public AiServiceController(AiServiceFacade aiServiceFacade) {
        this.aiServiceFacade = aiServiceFacade;
    }

    @GetMapping("/tools")
    public ApiResponse<List<AiServiceDtos.ToolResponse>> getTools() {
        return ApiResponse.ok(aiServiceFacade.getTools());
    }

    @PostMapping("/prompt-optimize")
    public ApiResponse<AiServiceDtos.PromptOptimizeResponse> optimizePrompt(
        @Valid @RequestBody AiServiceDtos.PromptOptimizeRequest request
    ) {
        return ApiResponse.ok(aiServiceFacade.optimizePrompt(request));
    }

    @PostMapping("/bio-assistant")
    public ApiResponse<AiServiceDtos.BioAssistantResponse> askBioAssistant(
        @Valid @RequestBody AiServiceDtos.BioAssistantRequest request
    ) {
        return ApiResponse.ok(aiServiceFacade.askBio(request));
    }
}
