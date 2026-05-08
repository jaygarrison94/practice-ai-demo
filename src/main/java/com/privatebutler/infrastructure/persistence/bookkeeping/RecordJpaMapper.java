package com.privatebutler.infrastructure.persistence.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordJpaMapper extends JpaRepository<Record, Long> {

    List<Record> findByUserIdAndStatusOrderByRecordDateDesc(Long userId, Integer status);

    List<Record> findByUserIdAndRecordDateBetweenAndStatus(Long userId, LocalDate start, LocalDate end, Integer status);

    List<Record> findByUserIdAndTypeAndStatus(Long userId, Integer type, Integer status);

    List<Record> findByUserIdAndCategoryIdAndStatus(Long userId, Long categoryId, Integer status);
}
