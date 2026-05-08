package com.privatebutler.interfaces.schedule;

import com.privatebutler.application.common.AuthApplicationService;
import com.privatebutler.application.schedule.ReminderService;
import com.privatebutler.application.schedule.ScheduleService;
import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.infrastructure.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ReminderService reminderService;
    private final AuthApplicationService authApplicationService;

    @PostMapping
    public ApiResponse<ScheduleVO> create(HttpServletRequest request, @Valid @RequestBody ScheduleCreateRequest req) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Schedule schedule = scheduleService.create(userId, req.getTitle(), req.getDate(), req.getTime(),
            req.getRemindBefore(), req.getNote(), req.getCategory(),
            req.getRepeatType(), req.getRepeatWeekDays(), req.getRepeatMonthDate(), req.getRepeatEndDate());
        return ApiResponse.success("日程创建成功", ScheduleVO.from(schedule));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleVO> update(HttpServletRequest request, @PathVariable Long id,
                                          @Valid @RequestBody ScheduleCreateRequest req) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Schedule schedule = scheduleService.update(id, userId, req.getTitle(), req.getDate(), req.getTime(),
            req.getRemindBefore(), req.getNote(), req.getCategory(),
            req.getRepeatType(), req.getRepeatWeekDays(), req.getRepeatMonthDate(), req.getRepeatEndDate());
        return ApiResponse.success("编辑成功", ScheduleVO.from(schedule));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = authApplicationService.getCurrentUserId(request);
        scheduleService.delete(id, userId);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleVO> getById(HttpServletRequest request, @PathVariable Long id) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Schedule schedule = scheduleService.getById(id, userId);
        return ApiResponse.success(ScheduleVO.from(schedule));
    }

    @GetMapping("/list")
    public ApiResponse<List<ScheduleVO>> list(HttpServletRequest request,
                                              @RequestParam(required = false) LocalDate date,
                                              @RequestParam(required = false) String category,
                                              @RequestParam(required = false) String keyword) {
        Long userId = authApplicationService.getCurrentUserId(request);
        List<Schedule> schedules;
        if (keyword != null && !keyword.isBlank()) {
            schedules = scheduleService.search(userId, keyword);
        } else if (category != null && !category.isBlank()) {
            schedules = scheduleService.listByCategory(userId, category);
        } else if (date != null) {
            schedules = scheduleService.listByDate(userId, date);
        } else {
            schedules = scheduleService.listByUser(userId);
        }
        List<ScheduleVO> vos = schedules.stream().map(ScheduleVO::from).toList();
        return ApiResponse.success(vos);
    }

    @GetMapping("/reminders")
    public ApiResponse<List<ScheduleVO>> checkReminders(HttpServletRequest request) {
        Long userId = authApplicationService.getCurrentUserId(request);
        List<Schedule> dueSchedules = reminderService.checkAndTriggerReminders(userId);
        List<ScheduleVO> vos = dueSchedules.stream().map(ScheduleVO::from).toList();
        return ApiResponse.success(vos);
    }
}
