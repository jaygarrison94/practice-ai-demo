package com.privatebutler.infrastructure.persistence.schedule;

import com.privatebutler.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleJpaMapper extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserIdAndStatusOrderByScheduleDateAsc(Long userId, Integer status);

    List<Schedule> findByUserIdAndScheduleDateAndStatus(Long userId, LocalDate date, Integer status);

    List<Schedule> findByUserIdAndCategoryAndStatus(Long userId, String category, Integer status);

    List<Schedule> findByUserIdAndTitleContainingAndStatus(Long userId, String keyword, Integer status);

    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.status = 1 " +
           "AND FUNCTION('TIMESTAMP', s.scheduleDate, s.scheduleTime) BETWEEN :now AND :window")
    List<Schedule> findUpcomingSchedules(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now,
                                         @Param("window") LocalDateTime window);
}
