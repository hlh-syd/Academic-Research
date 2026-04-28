package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.ResearchTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchTaskRepository extends JpaRepository<ResearchTask, Long> {

    List<ResearchTask> findByUserOrderByDueDateAscCreatedAtAsc(AppUser user);

    Optional<ResearchTask> findByIdAndUser(Long id, AppUser user);
}
