package com.privatebutler.domain.schedule.aggregate;

import com.privatebutler.domain.schedule.entity.RepeatRule;
import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.domain.schedule.valueobject.ReminderTime;
import com.privatebutler.domain.schedule.valueobject.ScheduleCategory;
import com.privatebutler.domain.schedule.valueobject.ScheduleTime;
import lombok.Getter;

@Getter
public class ScheduleAggregate {
    private final Schedule schedule;

    public ScheduleAggregate(Schedule schedule) {
        this.schedule = schedule;
    }

    public static ScheduleAggregate create(Long userId, String title, ScheduleTime scheduleTime,
                                           ReminderTime reminderTime, String note,
                                           ScheduleCategory category, RepeatRule repeatRule) {
        Schedule schedule = new Schedule(userId, title, scheduleTime, reminderTime, note, category, repeatRule);
        return new ScheduleAggregate(schedule);
    }

    public void edit(String title, ScheduleTime scheduleTime, ReminderTime reminderTime,
                     String note, ScheduleCategory category, RepeatRule repeatRule) {
        schedule.update(title, scheduleTime, reminderTime, note, category, repeatRule);
    }

    public void delete() {
        schedule.markDeleted();
    }
}
