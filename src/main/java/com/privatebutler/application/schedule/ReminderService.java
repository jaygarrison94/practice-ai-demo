package com.privatebutler.application.schedule;

import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.domain.schedule.service.ReminderTriggerService;
import com.privatebutler.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ScheduleRepository scheduleRepository;
    private final ReminderTriggerService reminderTriggerService;

    public List<Schedule> checkAndTriggerReminders(Long userId) {
        List<Schedule> dueSchedules = scheduleRepository.findDueReminders(userId);
        List<Schedule> toNotify = reminderTriggerService.findDueReminders(dueSchedules);
        for (Schedule schedule : toNotify) {
            log.info("Triggering reminder for schedule: userId={}, scheduleId={}, title={}",
                userId, schedule.getId(), schedule.getTitle());
        }
        return toNotify;
    }
}
