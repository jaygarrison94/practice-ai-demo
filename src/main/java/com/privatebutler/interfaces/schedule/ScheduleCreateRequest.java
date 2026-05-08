package com.privatebutler.interfaces.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleCreateRequest {
    @NotBlank(message = "请填写日程标题")
    private String title;

    private LocalDate date;

    private LocalTime time;

    private String remindBefore;

    private String note;

    private String category;

    private String repeatType;

    private String repeatWeekDays;

    private Integer repeatMonthDate;

    private LocalDate repeatEndDate;
}
