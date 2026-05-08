package com.privatebutler.domain.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatRule {

    @Column(name = "repeat_type")
    private String repeatType;

    @Column(name = "repeat_week_days")
    private String repeatWeekDays;

    @Column(name = "repeat_month_date")
    private Integer repeatMonthDate;

    @Column(name = "repeat_end_date")
    private LocalDate repeatEndDate;

    public boolean isRepeating() {
        return repeatType != null && !"NONE".equals(repeatType);
    }

    public List<Integer> getWeekDays() {
        if (repeatWeekDays == null || repeatWeekDays.isBlank()) return List.of();
        return Arrays.stream(repeatWeekDays.split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }
}
