package com.privatebutler.application.bookkeeping;

import com.privatebutler.domain.bookkeeping.aggregate.BookkeepingAggregate;
import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.repository.RecordRepository;
import com.privatebutler.domain.bookkeeping.valueobject.Amount;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    @Transactional
    public Record create(Long userId, Integer type, BigDecimal amount, Long categoryId,
                         String categoryName, String note, LocalDate recordDate) {
        if (recordDate == null) recordDate = LocalDate.now();
        RecordType recordType = RecordType.fromCode(type);
        Amount amt = new Amount(amount);
        BookkeepingAggregate aggregate = BookkeepingAggregate.create(
            userId, recordType, amt, categoryId, categoryName, note, recordDate);
        return recordRepository.save(aggregate.getRecord());
    }

    @Transactional
    public Record update(Long recordId, Long userId, Integer type, BigDecimal amount,
                         Long categoryId, String categoryName, String note, LocalDate recordDate) {
        Record record = recordRepository.findById(recordId)
            .orElseThrow(() -> new IllegalArgumentException("记账记录不存在"));
        if (!record.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该记录");
        }
        RecordType recordType = RecordType.fromCode(type);
        Amount amt = new Amount(amount);
        if (recordDate == null) recordDate = LocalDate.now();
        BookkeepingAggregate aggregate = new BookkeepingAggregate(record);
        aggregate.edit(recordType, amt, categoryId, categoryName, note, recordDate);
        return recordRepository.save(record);
    }

    @Transactional
    public void delete(Long recordId, Long userId) {
        Record record = recordRepository.findById(recordId)
            .orElseThrow(() -> new IllegalArgumentException("记账记录不存在"));
        if (!record.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该记录");
        }
        BookkeepingAggregate aggregate = new BookkeepingAggregate(record);
        aggregate.delete();
        recordRepository.save(record);
    }

    public Record getById(Long recordId, Long userId) {
        Record record = recordRepository.findById(recordId)
            .orElseThrow(() -> new IllegalArgumentException("记账记录不存在"));
        return record;
    }

    public List<Record> listByUser(Long userId) {
        return recordRepository.findByUserIdAndStatus(userId, 1);
    }

    public List<Record> listByDateRange(Long userId, LocalDate start, LocalDate end) {
        return recordRepository.findByUserIdAndRecordDateBetweenAndStatus(userId, start, end, 1);
    }

    public List<Record> listByType(Long userId, Integer type) {
        return recordRepository.findByUserIdAndTypeAndStatus(userId, type, 1);
    }
}
