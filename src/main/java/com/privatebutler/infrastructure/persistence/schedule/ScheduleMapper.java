package com.privatebutler.infrastructure.persistence.schedule;

import com.privatebutler.domain.schedule.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ScheduleMapper {

    Optional<Schedule> findById(Long id);

    List<Schedule> findByUserIdAndStatusOrderByScheduleDateAsc(@Param("userId") Long userId, @Param("status") Integer status);

    List<Schedule> findByUserIdAndScheduleDateAndStatus(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("status") Integer status);

    List<Schedule> findByUserIdAndCategoryAndStatus(@Param("userId") Long userId, @Param("category") String category, @Param("status") Integer status);

    List<Schedule> findByUserIdAndTitleContainingAndStatus(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("status") Integer status);

    List<Schedule> findUpcomingSchedules(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("window") LocalDateTime window);

    List<Schedule> findAll();

    int insert(Schedule schedule);

    int update(Schedule schedule);
}
