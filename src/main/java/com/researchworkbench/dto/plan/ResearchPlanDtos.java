package com.researchworkbench.dto.plan;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ResearchPlanDtos {

    private ResearchPlanDtos() {
    }

    public record TaskRequest(
        @NotBlank(message = "任务标题不能为空") String title,
        @NotBlank(message = "负责人不能为空") String ownerName,
        @NotNull(message = "截止日期不能为空") LocalDate dueDate,
        @NotBlank(message = "阶段不能为空") String lane,
        @NotNull(message = "进度不能为空")
        @Min(value = 0, message = "进度不能小于 0")
        @Max(value = 100, message = "进度不能大于 100")
        Integer progress
    ) {
    }

    public record TaskResponse(Long id, String title, String ownerName, LocalDate dueDate, String lane, Integer progress) {
    }

    public record CalendarEventRequest(
        @NotNull(message = "事件日期不能为空") LocalDate eventDate,
        @NotBlank(message = "事件标题不能为空") String title,
        @NotBlank(message = "标签不能为空") String tag
    ) {
    }

    public record CalendarEventResponse(Long id, LocalDate eventDate, String title, String tag) {
    }

    public record PunchResponse(String day, boolean done) {
    }

    public record FocusSessionRequest(
        @NotBlank(message = "会话类型不能为空") String type,
        @NotNull(message = "持续秒数不能为空")
        @Min(value = 1, message = "持续秒数至少 1 秒")
        Integer durationSeconds,
        @NotNull(message = "开始时间不能为空") LocalDateTime startedAt,
        @NotNull(message = "结束时间不能为空") LocalDateTime endedAt
    ) {
    }

    public record ResearchPlanOverviewResponse(
        List<TaskResponse> tasks,
        List<CalendarEventResponse> calendarEvents,
        List<PunchResponse> punches
    ) {
    }
}
