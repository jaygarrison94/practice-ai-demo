package com.privatebutler.domain.bookkeeping.repository;

import com.privatebutler.domain.common.BaseRepository;
import com.privatebutler.domain.bookkeeping.entity.Record;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends BaseRepository<Record> {
    List<Record> findByUserIdAndStatus(Long userId, Integer status);
    List<Record> findByUserIdAndRecordDateBetweenAndStatus(Long userId, LocalDate start, LocalDate end, Integer status);
    List<Record> findByUserIdAndTypeAndStatus(Long userId, Integer type, Integer status);
    List<Record> findByUserIdAndCategoryIdAndStatus(Long userId, Long categoryId, Integer status);
}
