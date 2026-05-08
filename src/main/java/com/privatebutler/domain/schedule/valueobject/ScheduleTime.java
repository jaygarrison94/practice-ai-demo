package com.privatebutler.domain.schedule.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@EqualsAndHashCode
public class ScheduleTime {
    private final LocalDate date;
    private final LocalTime time;

    public ScheduleTime(LocalDate date, LocalTime time) {
        if (date == null) {
            throw new IllegalArgumentException("请设置日程日期");
        }
        LocalDateTime scheduleDateTime = LocalDateTime.of(date, time != null ? time : LocalTime.MIDNIGHT);
        if (scheduleDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("请设置正确的日期和时间，不可选择过去时间");
        }
        this.date = date;
        this.time = time;
    }

    public LocalDateTime toDateTime() {
        return LocalDateTime.of(date, time != null ? time : LocalTime.MIDNIGHT);
    }
}
