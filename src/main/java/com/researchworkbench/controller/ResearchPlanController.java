package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.plan.ResearchPlanDtos;
import com.researchworkbench.service.ResearchPlanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/research-plan")
public class ResearchPlanController {

    private final ResearchPlanService researchPlanService;

    public ResearchPlanController(ResearchPlanService researchPlanService) {
        this.researchPlanService = researchPlanService;
    }

    @GetMapping("/overview")
    public ApiResponse<ResearchPlanDtos.ResearchPlanOverviewResponse> overview() {
        return ApiResponse.ok(researchPlanService.getOverview());
    }

    @PostMapping("/tasks")
    public ApiResponse<ResearchPlanDtos.TaskResponse> createTask(@Valid @RequestBody ResearchPlanDtos.TaskRequest request) {
        return ApiResponse.ok(researchPlanService.createTask(request));
    }

    @PutMapping("/tasks/{taskId}")
    public ApiResponse<ResearchPlanDtos.TaskResponse> updateTask(
        @PathVariable Long taskId,
        @Valid @RequestBody ResearchPlanDtos.TaskRequest request
    ) {
        return ApiResponse.ok(researchPlanService.updateTask(taskId, request));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ApiResponse<Void> deleteTask(@PathVariable Long taskId) {
        researchPlanService.deleteTask(taskId);
        return ApiResponse.ok(null, "Task deleted");
    }

    @PostMapping("/calendar-events")
    public ApiResponse<ResearchPlanDtos.CalendarEventResponse> createCalendarEvent(
        @Valid @RequestBody ResearchPlanDtos.CalendarEventRequest request
    ) {
        return ApiResponse.ok(researchPlanService.createCalendarEvent(request));
    }

    @PutMapping("/calendar-events/{eventId}")
    public ApiResponse<ResearchPlanDtos.CalendarEventResponse> updateCalendarEvent(
        @PathVariable Long eventId,
        @Valid @RequestBody ResearchPlanDtos.CalendarEventRequest request
    ) {
        return ApiResponse.ok(researchPlanService.updateCalendarEvent(eventId, request));
    }

    @DeleteMapping("/calendar-events/{eventId}")
    public ApiResponse<Void> deleteCalendarEvent(@PathVariable Long eventId) {
        researchPlanService.deleteCalendarEvent(eventId);
        return ApiResponse.ok(null, "Calendar event deleted");
    }

    @PostMapping("/punches")
    public ApiResponse<List<ResearchPlanDtos.PunchResponse>> punchToday() {
        return ApiResponse.ok(researchPlanService.punchToday());
    }

    @PostMapping("/focus-sessions")
    public ApiResponse<Void> recordFocusSession(@Valid @RequestBody ResearchPlanDtos.FocusSessionRequest request) {
        researchPlanService.recordFocusSession(request);
        return ApiResponse.ok(null, "Focus session recorded");
    }
}
