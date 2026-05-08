package com.privatebutler.infrastructure.persistence.schedule;

import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaMapper mapper;

    @Override
    public Optional<Schedule> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Schedule save(Schedule entity) {
        return mapper.save(entity);
    }

    @Override
    public void delete(Schedule entity) {
        mapper.delete(entity);
    }

    @Override
    public List<Schedule> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<Schedule> findByUserIdAndStatus(Long userId, Integer status) {
        return mapper.findByUserIdAndStatusOrderByScheduleDateAsc(userId, status);
    }

    @Override
    public List<Schedule> findByUserIdAndScheduleDateAndStatus(Long userId, LocalDate date, Integer status) {
        return mapper.findByUserIdAndScheduleDateAndStatus(userId, date, status);
    }

    @Override
    public List<Schedule> findByUserIdAndCategoryAndStatus(Long userId, String category, Integer status) {
        return mapper.findByUserIdAndCategoryAndStatus(userId, category, status);
    }

    @Override
    public List<Schedule> findByUserIdAndTitleContainingAndStatus(Long userId, String keyword, Integer status) {
        return mapper.findByUserIdAndTitleContainingAndStatus(userId, keyword, status);
    }

    @Override
    public List<Schedule> findDueReminders(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime window = now.plusMinutes(1);
        return mapper.findUpcomingSchedules(userId, now, window);
    }
}
