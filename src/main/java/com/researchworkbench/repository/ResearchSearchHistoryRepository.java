package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.ResearchSearchHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchSearchHistoryRepository extends JpaRepository<ResearchSearchHistory, Long> {

    List<ResearchSearchHistory> findTop10ByUserOrderByCreatedAtDesc(AppUser user);
}
