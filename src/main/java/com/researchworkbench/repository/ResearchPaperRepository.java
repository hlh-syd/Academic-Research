package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.ResearchPaper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {

    List<ResearchPaper> findByUserOrderByCreatedAtDesc(AppUser user);

    Optional<ResearchPaper> findByIdAndUser(Long id, AppUser user);

    List<ResearchPaper> findByUserAndTitleContainingIgnoreCaseOrUserAndSummaryContainingIgnoreCase(
        AppUser userForTitle,
        String title,
        AppUser userForSummary,
        String summary
    );
}
