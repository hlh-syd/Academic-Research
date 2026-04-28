package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.KnowledgeDocument;
import com.researchworkbench.domain.KnowledgeFolder;
import com.researchworkbench.domain.ResearchMindMapNode;
import com.researchworkbench.domain.ResearchPaper;
import com.researchworkbench.domain.ResearchQuickSummary;
import com.researchworkbench.domain.ResearchSearchHistory;
import com.researchworkbench.dto.research.ResearchDtos;
import com.researchworkbench.exception.NotFoundException;
import com.researchworkbench.repository.KnowledgeDocumentRepository;
import com.researchworkbench.repository.KnowledgeFolderRepository;
import com.researchworkbench.repository.ResearchMindMapNodeRepository;
import com.researchworkbench.repository.ResearchPaperRepository;
import com.researchworkbench.repository.ResearchQuickSummaryRepository;
import com.researchworkbench.repository.ResearchSearchHistoryRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResearchService {

    private final CurrentUserService currentUserService;
    private final ResearchPaperRepository researchPaperRepository;
    private final ResearchSearchHistoryRepository researchSearchHistoryRepository;
    private final ResearchMindMapNodeRepository researchMindMapNodeRepository;
    private final ResearchQuickSummaryRepository researchQuickSummaryRepository;
    private final KnowledgeFolderRepository knowledgeFolderRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;

    public ResearchService(
        CurrentUserService currentUserService,
        ResearchPaperRepository researchPaperRepository,
        ResearchSearchHistoryRepository researchSearchHistoryRepository,
        ResearchMindMapNodeRepository researchMindMapNodeRepository,
        ResearchQuickSummaryRepository researchQuickSummaryRepository,
        KnowledgeFolderRepository knowledgeFolderRepository,
        KnowledgeDocumentRepository knowledgeDocumentRepository
    ) {
        this.currentUserService = currentUserService;
        this.researchPaperRepository = researchPaperRepository;
        this.researchSearchHistoryRepository = researchSearchHistoryRepository;
        this.researchMindMapNodeRepository = researchMindMapNodeRepository;
        this.researchQuickSummaryRepository = researchQuickSummaryRepository;
        this.knowledgeFolderRepository = knowledgeFolderRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
    }

    @Transactional
    public ResearchDtos.ResearchOverviewResponse getOverview(String keyword) {
        AppUser user = currentUserService.getCurrentUser();
        List<ResearchPaper> papers;
        if (keyword != null && !keyword.isBlank()) {
            ResearchSearchHistory history = new ResearchSearchHistory();
            history.setUser(user);
            history.setKeyword(keyword.trim());
            history.setHits(researchPaperRepository.findByUserOrderByCreatedAtDesc(user).size());
            history.setTimeLabel("刚刚");
            researchSearchHistoryRepository.save(history);
            papers = researchPaperRepository.findByUserAndTitleContainingIgnoreCaseOrUserAndSummaryContainingIgnoreCase(
                user, keyword.trim(), user, keyword.trim()
            );
        } else {
            papers = researchPaperRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return new ResearchDtos.ResearchOverviewResponse(
            papers.stream().map(this::toPaperResponse).toList(),
            researchSearchHistoryRepository.findTop10ByUserOrderByCreatedAtDesc(user).stream().map(this::toHistoryResponse).toList(),
            researchMindMapNodeRepository.findByUserOrderByCreatedAtAsc(user).stream().map(this::toMindMapResponse).toList(),
            researchQuickSummaryRepository.findByUserOrderByCreatedAtAsc(user).stream().map(this::toQuickSummaryResponse).toList()
        );
    }

    @Transactional
    public ResearchDtos.PaperResponse collectToKnowledgeBase(Long paperId, Long folderId) {
        AppUser user = currentUserService.getCurrentUser();
        ResearchPaper paper = researchPaperRepository.findByIdAndUser(paperId, user)
            .orElseThrow(() -> new NotFoundException("Paper not found"));
        KnowledgeFolder folder = knowledgeFolderRepository.findByIdAndUser(folderId, user)
            .orElseThrow(() -> new NotFoundException("Knowledge folder not found"));

        KnowledgeDocument document = new KnowledgeDocument();
        document.setFolder(folder);
        document.setOriginalName(paper.getTitle() + ".md");
        document.setStoragePath("generated:" + paper.getTitle());
        document.setContentType("text/markdown");
        document.setSizeBytes((long) paper.getSummary().length());
        document.setSizeLabel("Generated");
        document.setUpdatedAtLabel("刚刚");
        document.setSummary("由论文搜索结果一键加入: " + paper.getSummary());
        knowledgeDocumentRepository.save(document);

        paper.setInKnowledgeBase(true);
        return toPaperResponse(researchPaperRepository.save(paper));
    }

    private ResearchDtos.PaperResponse toPaperResponse(ResearchPaper paper) {
        List<String> tags = Arrays.stream(paper.getTags().split(","))
            .map(String::trim)
            .filter(tag -> !tag.isBlank())
            .toList();
        return new ResearchDtos.PaperResponse(
            paper.getId(),
            paper.getTitle(),
            paper.getAuthors(),
            paper.getPublicationYear(),
            paper.getJournal(),
            paper.getSummary(),
            tags,
            paper.getCitedBy(),
            paper.isInKnowledgeBase()
        );
    }

    private ResearchDtos.HistoryResponse toHistoryResponse(ResearchSearchHistory history) {
        return new ResearchDtos.HistoryResponse(history.getId(), history.getKeyword(), history.getTimeLabel(), history.getHits());
    }

    private ResearchDtos.MindMapNodeResponse toMindMapResponse(ResearchMindMapNode node) {
        return new ResearchDtos.MindMapNodeResponse(node.getId(), node.getTitle(), node.getDetail(), node.isAccent());
    }

    private ResearchDtos.QuickSummaryResponse toQuickSummaryResponse(ResearchQuickSummary summary) {
        return new ResearchDtos.QuickSummaryResponse(summary.getId(), summary.getLabel(), summary.getContent(), summary.getTone());
    }
}
