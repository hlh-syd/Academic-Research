package com.researchworkbench.repository;

import com.researchworkbench.domain.AiToolLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiToolLinkRepository extends JpaRepository<AiToolLink, Long> {

    List<AiToolLink> findAllByOrderBySortOrderAsc();
}
