package com.privatebutler.infrastructure.persistence.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecordRepositoryImpl implements RecordRepository {

    private final RecordMapper mapper;

    @Override
    public Optional<Record> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Record save(Record entity) {
        if (entity.getId() == null) {
            entity.onCreate();
            mapper.insert(entity);
        } else {
            entity.onUpdate();
            mapper.update(entity);
        }
        return entity;
    }

    @Override
    public void delete(Record entity) {
        entity.onUpdate();
        mapper.update(entity);
    }

    @Override
    public List<Record> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<Record> findByUserIdAndStatus(Long userId, Integer status) {
        return mapper.findByUserIdAndStatusOrderByRecordDateDesc(userId, status);
    }

    @Override
    public List<Record> findByUserIdAndRecordDateBetweenAndStatus(Long userId, LocalDate start, LocalDate end, Integer status) {
        return mapper.findByUserIdAndRecordDateBetweenAndStatus(userId, start, end, status);
    }

    @Override
    public List<Record> findByUserIdAndTypeAndStatus(Long userId, Integer type, Integer status) {
        return mapper.findByUserIdAndTypeAndStatus(userId, type, status);
    }

    @Override
    public List<Record> findByUserIdAndCategoryIdAndStatus(Long userId, Long categoryId, Integer status) {
        return mapper.findByUserIdAndCategoryIdAndStatus(userId, categoryId, status);
    }
}
