package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.ResearchMindMapNode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchMindMapNodeRepository extends JpaRepository<ResearchMindMapNode, Long> {

    List<ResearchMindMapNode> findByUserOrderByCreatedAtAsc(AppUser user);
}
