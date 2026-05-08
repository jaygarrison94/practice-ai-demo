package com.privatebutler.domain.bookkeeping.aggregate;

import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.valueobject.Amount;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookkeepingAggregate {
    private final Record record;

    public BookkeepingAggregate(Record record) {
        this.record = record;
    }

    public static BookkeepingAggregate create(Long userId, RecordType type, Amount amount,
                                               Long categoryId, String categoryName,
                                               String note, LocalDate recordDate) {
        Record record = new Record(userId, type, amount, categoryId, categoryName, note, recordDate);
        return new BookkeepingAggregate(record);
    }

    public void edit(RecordType type, Amount amount, Long categoryId,
                     String categoryName, String note, LocalDate recordDate) {
        record.update(type, amount, categoryId, categoryName, note, recordDate);
    }

    public void delete() {
        record.markDeleted();
    }
}
