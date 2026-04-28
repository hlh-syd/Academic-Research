package com.researchworkbench.repository;

import com.researchworkbench.domain.KnowledgeDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

    List<KnowledgeDocument> findByFolderIdAndOriginalNameContainingIgnoreCaseOrFolderIdAndSummaryContainingIgnoreCase(
        Long folderIdForName,
        String originalName,
        Long folderIdForSummary,
        String summary
    );

    List<KnowledgeDocument> findByIdIn(List<Long> ids);
}
