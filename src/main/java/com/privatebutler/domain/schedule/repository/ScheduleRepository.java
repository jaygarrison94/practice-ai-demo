package com.privatebutler.domain.schedule.repository;

import com.privatebutler.domain.common.BaseRepository;
import com.privatebutler.domain.schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends BaseRepository<Schedule> {
    List<Schedule> findByUserIdAndStatus(Long userId, Integer status);
    List<Schedule> findByUserIdAndScheduleDateAndStatus(Long userId, LocalDate date, Integer status);
    List<Schedule> findByUserIdAndCategoryAndStatus(Long userId, String category, Integer status);
    List<Schedule> findByUserIdAndTitleContainingAndStatus(Long userId, String keyword, Integer status);
    List<Schedule> findDueReminders(Long userId);
}
