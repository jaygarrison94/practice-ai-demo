package com.privatebutler.domain.schedule.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class ReminderTime {
    private final String value;
    private final Duration duration;

    private ReminderTime(String value, Duration duration) {
        this.value = value;
        this.duration = duration;
    }

    public static final ReminderTime FIVE_MINUTES = new ReminderTime("5m", Duration.ofMinutes(5));
    public static final ReminderTime TEN_MINUTES = new ReminderTime("10m", Duration.ofMinutes(10));
    public static final ReminderTime THIRTY_MINUTES = new ReminderTime("30m", Duration.ofMinutes(30));
    public static final ReminderTime ONE_HOUR = new ReminderTime("1h", Duration.ofHours(1));
    public static final ReminderTime ONE_DAY = new ReminderTime("1d", Duration.ofDays(1));

    public static ReminderTime fromValue(String value) {
        return switch (value) {
            case "5m" -> FIVE_MINUTES;
            case "10m" -> TEN_MINUTES;
            case "30m" -> THIRTY_MINUTES;
            case "1h" -> ONE_HOUR;
            case "1d" -> ONE_DAY;
            default -> TEN_MINUTES;
        };
    }

    public LocalDateTime calculateRemindAt(LocalDateTime scheduleDateTime) {
        if (scheduleDateTime.minus(duration).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("提醒时间不可晚于日程时间");
        }
        return scheduleDateTime.minus(duration);
    }
}
