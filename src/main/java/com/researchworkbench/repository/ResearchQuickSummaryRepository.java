package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.ResearchQuickSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchQuickSummaryRepository extends JpaRepository<ResearchQuickSummary, Long> {

    List<ResearchQuickSummary> findByUserOrderByCreatedAtAsc(AppUser user);
}
