package com.privatebutler.domain.schedule.entity;

import com.privatebutler.domain.common.BaseEntity;
import com.privatebutler.domain.schedule.valueobject.ReminderTime;
import com.privatebutler.domain.schedule.valueobject.ScheduleCategory;
import com.privatebutler.domain.schedule.valueobject.ScheduleTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sch_schedule")
@Getter
@Setter
@NoArgsConstructor
public class Schedule extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "schedule_date", nullable = false)
    private java.time.LocalDate scheduleDate;

    @Column(name = "schedule_time", nullable = false)
    private java.time.LocalTime scheduleTime;

    @Column(name = "remind_before")
    private String remindBefore;

    @Column(length = 500)
    private String note;

    @Column(length = 20)
    private String category;

    @Embedded
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
