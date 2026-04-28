package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.PunchRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PunchRecordRepository extends JpaRepository<PunchRecord, Long> {

    List<PunchRecord> findByUserAndPunchDateBetweenOrderByPunchDateAsc(AppUser user, LocalDate start, LocalDate end);

    Optional<PunchRecord> findByUserAndPunchDate(AppUser user, LocalDate punchDate);
}
