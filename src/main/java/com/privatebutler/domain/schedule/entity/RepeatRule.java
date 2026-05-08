package com.privatebutler.domain.schedule.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatRule {

    private String repeatType;

    private String repeatWeekDays;

    private Integer repeatMonthDate;

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
