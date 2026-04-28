package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.WebSearchHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebSearchHistoryRepository extends JpaRepository<WebSearchHistory, Long> {

    List<WebSearchHistory> findTop20ByUserOrderByCreatedAtDesc(AppUser user);

    Optional<WebSearchHistory> findByIdAndUser(Long id, AppUser user);
}
