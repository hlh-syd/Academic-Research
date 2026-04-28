package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.KnowledgeFolder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeFolderRepository extends JpaRepository<KnowledgeFolder, Long> {

    @EntityGraph(attributePaths = "documents")
    List<KnowledgeFolder> findByUserOrderByCreatedAtAsc(AppUser user);

    @EntityGraph(attributePaths = "documents")
    Optional<KnowledgeFolder> findByIdAndUser(Long id, AppUser user);
}
