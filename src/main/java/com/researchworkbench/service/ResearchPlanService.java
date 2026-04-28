package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.CalendarEvent;
import com.researchworkbench.domain.FocusSession;
import com.researchworkbench.domain.FocusSessionType;
import com.researchworkbench.domain.PunchRecord;
import com.researchworkbench.domain.ResearchTask;
import com.researchworkbench.dto.plan.ResearchPlanDtos;
import com.researchworkbench.exception.BadRequestException;
import com.researchworkbench.exception.NotFoundException;
import com.researchworkbench.repository.CalendarEventRepository;
import com.researchworkbench.repository.FocusSessionRepository;
import com.researchworkbench.repository.PunchRecordRepository;
import com.researchworkbench.repository.ResearchTaskRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResearchPlanService {

    private final CurrentUserService currentUserService;
    private final ResearchTaskRepository researchTaskRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final PunchRecordRepository punchRecordRepository;
    private final FocusSessionRepository focusSessionRepository;

    public ResearchPlanService(
        CurrentUserService currentUserService,
        ResearchTaskRepository researchTaskRepository,
        CalendarEventRepository calendarEventRepository,
        PunchRecordRepository punchRecordRepository,
        FocusSessionRepository focusSessionRepository
    ) {
        this.currentUserService = currentUserService;
        this.researchTaskRepository = researchTaskRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.punchRecordRepository = punchRecordRepository;
        this.focusSessionRepository = focusSessionRepository;
    }

    @Transactional(readOnly = true)
    public ResearchPlanDtos.ResearchPlanOverviewResponse getOverview() {
        AppUser user = currentUserService.getCurrentUser();
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate weekStart = now.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        return new ResearchPlanDtos.ResearchPlanOverviewResponse(
            researchTaskRepository.findByUserOrderByDueDateAscCreatedAtAsc(user).stream().map(this::toTaskResponse).toList(),
            calendarEventRepository.findByUserAndEventDateBetweenOrderByEventDateAsc(user, monthStart, monthEnd).stream().map(this::toCalendarResponse).toList(),
            toPunchResponses(punchRecordRepository.findByUserAndPunchDateBetweenOrderByPunchDateAsc(user, weekStart, weekEnd), weekStart)
        );
    }

    @Transactional
    public ResearchPlanDtos.TaskResponse createTask(ResearchPlanDtos.TaskRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        ResearchTask task = new ResearchTask();
        task.setUser(user);
        applyTask(task, request);
        return toTaskResponse(researchTaskRepository.save(task));
    }

    @Transactional
    public ResearchPlanDtos.TaskResponse updateTask(Long taskId, ResearchPlanDtos.TaskRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        ResearchTask task = researchTaskRepository.findByIdAndUser(taskId, user)
            .orElseThrow(() -> new NotFoundException("Task not found"));
        applyTask(task, request);
        return toTaskResponse(researchTaskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId) {
        AppUser user = currentUserService.getCurrentUser();
        ResearchTask task = researchTaskRepository.findByIdAndUser(taskId, user)
            .orElseThrow(() -> new NotFoundException("Task not found"));
        researchTaskRepository.delete(task);
    }

    @Transactional
    public ResearchPlanDtos.CalendarEventResponse createCalendarEvent(ResearchPlanDtos.CalendarEventRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        CalendarEvent event = new CalendarEvent();
        event.setUser(user);
        applyCalendarEvent(event, request);
        return toCalendarResponse(calendarEventRepository.save(event));
    }

    @Transactional
    public ResearchPlanDtos.CalendarEventResponse updateCalendarEvent(Long eventId, ResearchPlanDtos.CalendarEventRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        CalendarEvent event = calendarEventRepository.findByIdAndUser(eventId, user)
            .orElseThrow(() -> new NotFoundException("Calendar event not found"));
        applyCalendarEvent(event, request);
        return toCalendarResponse(calendarEventRepository.save(event));
    }

    @Transactional
    public void deleteCalendarEvent(Long eventId) {
        AppUser user = currentUserService.getCurrentUser();
        CalendarEvent event = calendarEventRepository.findByIdAndUser(eventId, user)
            .orElseThrow(() -> new NotFoundException("Calendar event not found"));
        calendarEventRepository.delete(event);
    }

    @Transactional
    public List<ResearchPlanDtos.PunchResponse> punchToday() {
        AppUser user = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();
        PunchRecord record = punchRecordRepository.findByUserAndPunchDate(user, today).orElseGet(() -> {
            PunchRecord created = new PunchRecord();
            created.setUser(user);
            created.setPunchDate(today);
            return created;
        });
        record.setCompleted(true);
        punchRecordRepository.save(record);

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        return toPunchResponses(punchRecordRepository.findByUserAndPunchDateBetweenOrderByPunchDateAsc(user, weekStart, weekEnd), weekStart);
    }

    @Transactional
    public void recordFocusSession(ResearchPlanDtos.FocusSessionRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        FocusSession session = new FocusSession();
        session.setUser(user);
        session.setType(parseType(request.type()));
        session.setDurationSeconds(request.durationSeconds());
        session.setStartedAt(request.startedAt());
        session.setEndedAt(request.endedAt());
        focusSessionRepository.save(session);
    }

    private void applyTask(ResearchTask task, ResearchPlanDtos.TaskRequest request) {
        task.setTitle(request.title().trim());
        task.setOwnerName(request.ownerName().trim());
        task.setDueDate(request.dueDate());
        task.setLane(request.lane().trim());
        task.setProgress(request.progress());
    }

    private void applyCalendarEvent(CalendarEvent event, ResearchPlanDtos.CalendarEventRequest request) {
        event.setEventDate(request.eventDate());
        event.setTitle(request.title().trim());
        event.setTag(request.tag().trim());
    }

    private FocusSessionType parseType(String raw) {
        return switch (raw.trim().toUpperCase()) {
            case "POMODORO" -> FocusSessionType.POMODORO;
            case "TIMER" -> FocusSessionType.TIMER;
            default -> throw new BadRequestException("Unsupported focus session type: " + raw);
        };
    }

    private ResearchPlanDtos.TaskResponse toTaskResponse(ResearchTask task) {
        return new ResearchPlanDtos.TaskResponse(task.getId(), task.getTitle(), task.getOwnerName(), task.getDueDate(), task.getLane(), task.getProgress());
    }

    private ResearchPlanDtos.CalendarEventResponse toCalendarResponse(CalendarEvent event) {
        return new ResearchPlanDtos.CalendarEventResponse(event.getId(), event.getEventDate(), event.getTitle(), event.getTag());
    }

    private List<ResearchPlanDtos.PunchResponse> toPunchResponses(List<PunchRecord> records, LocalDate weekStart) {
        List<ResearchPlanDtos.PunchResponse> responses = new ArrayList<>();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate day = weekStart.plusDays(offset);
            boolean done = records.stream().anyMatch(record -> record.getPunchDate().equals(day) && record.isCompleted());
            responses.add(new ResearchPlanDtos.PunchResponse(day.getDayOfWeek().name().substring(0, 3), done));
        }
        return responses;
    }
}
