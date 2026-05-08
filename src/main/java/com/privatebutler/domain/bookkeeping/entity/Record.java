package com.privatebutler.domain.bookkeeping.entity;

import com.privatebutler.domain.bookkeeping.valueobject.Amount;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import com.privatebutler.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Record extends BaseEntity {

    private Long userId;

    private Integer type;

    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private String note;

    private LocalDate recordDate;

    public Record(Long userId, RecordType type, Amount amount, Long categoryId,
                  String categoryName, String note, LocalDate recordDate) {
        this.userId = userId;
        this.type = type.getCode();
        this.amount = amount.getValue();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.note = note;
        this.recordDate = recordDate;
        this.status = 1;
    }

    public void update(RecordType type, Amount amount, Long categoryId,
                       String categoryName, String note, LocalDate recordDate) {
        this.type = type.getCode();
        this.amount = amount.getValue();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.note = note;
        this.recordDate = recordDate;
    }

    public RecordType getRecordType() {
        return RecordType.fromCode(type);
    }

    public String getAmountDisplay() {
        String prefix = type == 2 ? "+" : "-";
        return prefix + amount.toPlainString();
    }

    public void markDeleted() {
        this.status = 0;
    }
}
