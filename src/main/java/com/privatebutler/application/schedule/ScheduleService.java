package com.privatebutler.application.schedule;

import com.privatebutler.domain.schedule.aggregate.ScheduleAggregate;
import com.privatebutler.domain.schedule.entity.RepeatRule;
import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.domain.schedule.repository.ScheduleRepository;
import com.privatebutler.domain.schedule.valueobject.ReminderTime;
import com.privatebutler.domain.schedule.valueobject.ScheduleCategory;
import com.privatebutler.domain.schedule.valueobject.ScheduleTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule create(Long userId, String title, LocalDate date, LocalTime time,
                           String remindBefore, String note, String category,
                           String repeatType, String repeatWeekDays, Integer repeatMonthDate,
                           LocalDate repeatEndDate) {
        ScheduleTime scheduleTime = new ScheduleTime(date, time);
        ReminderTime reminderTime = ReminderTime.fromValue(remindBefore);
        ScheduleCategory scheduleCategory = ScheduleCategory.fromString(category);

        RepeatRule repeatRule = RepeatRule.builder()
            .repeatType(repeatType != null ? repeatType : "NONE")
            .repeatWeekDays(repeatWeekDays)
            .repeatMonthDate(repeatMonthDate)
            .repeatEndDate(repeatEndDate)
            .build();

        ScheduleAggregate aggregate = ScheduleAggregate.create(
            userId, title, scheduleTime, reminderTime, note, scheduleCategory, repeatRule);
        return scheduleRepository.save(aggregate.getSchedule());
    }

    @Transactional
    public Schedule update(Long scheduleId, Long userId, String title, LocalDate date, LocalTime time,
                           String remindBefore, String note, String category,
                           String repeatType, String repeatWeekDays, Integer repeatMonthDate,
                           LocalDate repeatEndDate) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该日程");
        }
        ScheduleTime scheduleTime = new ScheduleTime(date, time);
        ReminderTime reminderTime = ReminderTime.fromValue(remindBefore);
        ScheduleCategory scheduleCategory = ScheduleCategory.fromString(category);

        RepeatRule repeatRule = RepeatRule.builder()
            .repeatType(repeatType != null ? repeatType : "NONE")
            .repeatWeekDays(repeatWeekDays)
            .repeatMonthDate(repeatMonthDate)
            .repeatEndDate(repeatEndDate)
            .build();

        ScheduleAggregate aggregate = new ScheduleAggregate(schedule);
        aggregate.edit(title, scheduleTime, reminderTime, note, scheduleCategory, repeatRule);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void delete(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该日程");
        }
        ScheduleAggregate aggregate = new ScheduleAggregate(schedule);
        aggregate.delete();
        scheduleRepository.save(schedule);
    }

    public Schedule getById(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("日程不存在"));
        return schedule;
    }

    public List<Schedule> listByUser(Long userId) {
        return scheduleRepository.findByUserIdAndStatus(userId, 1);
    }

    public List<Schedule> listByDate(Long userId, LocalDate date) {
        return scheduleRepository.findByUserIdAndScheduleDateAndStatus(userId, date, 1);
    }

    public List<Schedule> listByCategory(Long userId, String category) {
        return scheduleRepository.findByUserIdAndCategoryAndStatus(userId, category, 1);
    }

    public List<Schedule> search(Long userId, String keyword) {
        return scheduleRepository.findByUserIdAndTitleContainingAndStatus(userId, keyword, 1);
    }
}
