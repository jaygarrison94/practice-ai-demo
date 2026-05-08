package com.privatebutler.interfaces.schedule;

import com.privatebutler.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ScheduleVO {
    private Long id;
    private String title;
    private LocalDate scheduleDate;
    private LocalTime scheduleTime;
    private String remindBefore;
    private String note;
    private String category;
    private String repeatType;

    public static ScheduleVO from(Schedule schedule) {
        return new ScheduleVO(
            schedule.getId(), schedule.getTitle(),
            schedule.getScheduleDate(), schedule.getScheduleTime(),
            schedule.getRemindBefore(), schedule.getNote(),
            schedule.getCategory(),
            schedule.getRepeatRule() != null ? schedule.getRepeatRule().getRepeatType() : null
        );
    }
}
