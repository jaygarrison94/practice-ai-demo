package com.privatebutler.interfaces.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RecordVO {
    private Long id;
    private Integer type;
    private String typeName;
    private BigDecimal amount;
    private String amountDisplay;
    private Long categoryId;
    private String categoryName;
    private String note;
    private LocalDate recordDate;

    public static RecordVO from(Record record) {
        return new RecordVO(
            record.getId(), record.getType(),
            RecordType.fromCode(record.getType()).getDisplayName(),
            record.getAmount(), record.getAmountDisplay(),
            record.getCategoryId(), record.getCategoryName(),
            record.getNote(), record.getRecordDate()
        );
    }
}
