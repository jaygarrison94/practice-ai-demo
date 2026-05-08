package com.privatebutler.domain.schedule.entity;

import com.privatebutler.domain.common.BaseEntity;
import com.privatebutler.domain.schedule.valueobject.ReminderTime;
import com.privatebutler.domain.schedule.valueobject.ScheduleCategory;
import com.privatebutler.domain.schedule.valueobject.ScheduleTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Schedule extends BaseEntity {

    private Long userId;

    private String title;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

    private String remindBefore;

    private String note;

    private String category;

    private RepeatRule repeatRule;

    public Schedule(Long userId, String title, ScheduleTime scheduleTime, ReminderTime reminderTime,
                    String note, ScheduleCategory category, RepeatRule repeatRule) {
        this.userId = userId;
        this.title = title;
        this.scheduleDate = scheduleTime.getDate();
        this.scheduleTime = scheduleTime.getTime();
        this.remindBefore = reminderTime.getValue();
        this.note = note;
        this.category = category.name();
        this.repeatRule = repeatRule;
        this.status = 1;
    }

    public void update(String title, ScheduleTime scheduleTime, ReminderTime reminderTime,
                       String note, ScheduleCategory category, RepeatRule repeatRule) {
        this.title = title;
        this.scheduleDate = scheduleTime.getDate();
        this.scheduleTime = scheduleTime.getTime();
        this.remindBefore = reminderTime.getValue();
        this.note = note;
        this.category = category.name();
        this.repeatRule = repeatRule;
    }

    public LocalDateTime getScheduleDateTime() {
        return LocalDateTime.of(scheduleDate, scheduleTime);
    }

    public void markDeleted() {
        this.status = 0;
    }
}
