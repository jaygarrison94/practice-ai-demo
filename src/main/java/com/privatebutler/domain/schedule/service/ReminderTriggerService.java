package com.privatebutler.domain.schedule.service;

import com.privatebutler.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderTriggerService {

    public List<Schedule> findDueReminders(List<Schedule> schedules) {
        LocalDateTime now = LocalDateTime.now();
        return schedules.stream()
            .filter(s -> s.getStatus() == 1)
            .filter(s -> {
                LocalDateTime remindAt = calculateRemindAt(s);
                Duration diff = Duration.between(remindAt, now);
                return diff.toSeconds() >= 0 && diff.toMinutes() <= 1;
            })
            .toList();
    }

    private LocalDateTime calculateRemindAt(Schedule schedule) {
        LocalDateTime scheduleTime = LocalDateTime.of(schedule.getScheduleDate(), schedule.getScheduleTime());
        String remindBefore = schedule.getRemindBefore();
        if (remindBefore == null) remindBefore = "10m";
        return switch (remindBefore) {
            case "5m" -> scheduleTime.minusMinutes(5);
            case "10m" -> scheduleTime.minusMinutes(10);
            case "30m" -> scheduleTime.minusMinutes(30);
            case "1h" -> scheduleTime.minusHours(1);
            case "1d" -> scheduleTime.minusDays(1);
            default -> scheduleTime.minusMinutes(10);
        };
    }
}
