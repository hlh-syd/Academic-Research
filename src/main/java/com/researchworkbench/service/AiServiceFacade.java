package com.researchworkbench.service;

import com.researchworkbench.dto.ai.AiServiceDtos;
import com.researchworkbench.repository.AiToolLinkRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiServiceFacade {

    private final AiToolLinkRepository aiToolLinkRepository;

    public AiServiceFacade(AiToolLinkRepository aiToolLinkRepository) {
        this.aiToolLinkRepository = aiToolLinkRepository;
    }

    public List<AiServiceDtos.ToolResponse> getTools() {
        return aiToolLinkRepository.findAllByOrderBySortOrderAsc().stream()
            .map(tool -> new AiServiceDtos.ToolResponse(tool.getId(), tool.getName(), tool.getDescription(), tool.getUrl(), tool.getTag()))
            .toList();
    }

    public AiServiceDtos.PromptOptimizeResponse optimizePrompt(AiServiceDtos.PromptOptimizeRequest request) {
        String optimized = """
            你是一名严谨的生物医学综述助手。
            任务：%s
            输出要求：
            1. 按“研究问题 / 方法 / 结果 / 局限 / 引用证据”组织
            2. 每个结论后都附来源标记
            3. 如果证据不足，必须明确说明
            """.formatted(request.draft().trim());
        return new AiServiceDtos.PromptOptimizeResponse(optimized);
    }

    public AiServiceDtos.BioAssistantResponse askBio(AiServiceDtos.BioAssistantRequest request) {
        String answer = "Bio 助手建议按“疾病 / 样本 / 实验手段 / 终点指标 / 来源”回答该问题: " + request.question().trim();
        return new AiServiceDtos.BioAssistantResponse(answer);
    }
}
