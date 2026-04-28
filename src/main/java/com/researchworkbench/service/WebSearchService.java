package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.SearchSource;
import com.researchworkbench.domain.WebSearchHistory;
import com.researchworkbench.dto.web.WebSearchDtos;
import com.researchworkbench.exception.BadRequestException;
import com.researchworkbench.exception.NotFoundException;
import com.researchworkbench.repository.WebSearchHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebSearchService {

    private final CurrentUserService currentUserService;
    private final WebSearchHistoryRepository webSearchHistoryRepository;

    public WebSearchService(CurrentUserService currentUserService, WebSearchHistoryRepository webSearchHistoryRepository) {
        this.currentUserService = currentUserService;
        this.webSearchHistoryRepository = webSearchHistoryRepository;
    }

    @Transactional
    public WebSearchDtos.SearchResponse execute(WebSearchDtos.ExecuteRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        SearchSource source = parseSource(request.source());
        String markdown = buildMarkdown(request.query(), source);
        String excerpt = switch (source) {
            case WEB -> "网页结果已整理为 Markdown 摘要，适合继续入库。";
            case DOUYIN -> "抖音结果更适合热点抓取与表达方式观察。";
            case XIAOHONGSHU -> "小红书结果更适合图文清单、经验贴和踩坑总结。";
        };

        WebSearchHistory history = new WebSearchHistory();
        history.setUser(user);
        history.setSource(source);
        history.setQuery(request.query().trim());
        history.setExcerpt(excerpt);
        history.setMarkdownContent(markdown);
        history.setSaved(false);
        return toResponse(webSearchHistoryRepository.save(history));
    }

    @Transactional(readOnly = true)
    public List<WebSearchDtos.SearchResponse> getHistory() {
        AppUser user = currentUserService.getCurrentUser();
        return webSearchHistoryRepository.findTop20ByUserOrderByCreatedAtDesc(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public WebSearchDtos.SearchResponse save(Long historyId) {
        AppUser user = currentUserService.getCurrentUser();
        WebSearchHistory history = webSearchHistoryRepository.findByIdAndUser(historyId, user)
            .orElseThrow(() -> new NotFoundException("Web search history not found"));
        history.setSaved(true);
        return toResponse(webSearchHistoryRepository.save(history));
    }

    private SearchSource parseSource(String raw) {
        return switch (raw.trim()) {
            case "网页", "WEB" -> SearchSource.WEB;
            case "抖音", "DOUYIN" -> SearchSource.DOUYIN;
            case "小红书", "XIAOHONGSHU" -> SearchSource.XIAOHONGSHU;
            default -> throw new BadRequestException("Unsupported search source: " + raw);
        };
    }

    private String buildMarkdown(String query, SearchSource source) {
        return switch (source) {
            case WEB -> "# " + query + "\n\n## 检索摘要\n- 网页内容更偏标准文档、综述与技术博客。\n- 建议提取概念词与可追溯来源。\n\n## 下一步\n1. 标注来源\n2. 摘出结论\n3. 加入知识库";
            case DOUYIN -> "# " + query + "\n\n- 适合观察高频表达方式与热点切口。\n- 建议保留前 3 秒问题定义与结论句式。";
            case XIAOHONGSHU -> "# " + query + "\n\n## 用户观察\n- 图文清单和避坑总结更受欢迎。\n- 可以反向抽取高频问题词。";
        };
    }

    private WebSearchDtos.SearchResponse toResponse(WebSearchHistory history) {
        return new WebSearchDtos.SearchResponse(
            history.getId(),
            history.getSource().name(),
            history.getQuery(),
            history.getExcerpt(),
            history.getMarkdownContent(),
            history.isSaved()
        );
    }
}
