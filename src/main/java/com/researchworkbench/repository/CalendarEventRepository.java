package com.researchworkbench.repository;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.CalendarEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByUserAndEventDateBetweenOrderByEventDateAsc(AppUser user, LocalDate start, LocalDate end);

    Optional<CalendarEvent> findByIdAndUser(Long id, AppUser user);
}
