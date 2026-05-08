package com.privatebutler.domain.bookkeeping.entity;

import com.privatebutler.domain.bookkeeping.valueobject.Amount;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import com.privatebutler.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bk_record")
@Getter
@Setter
@NoArgsConstructor
public class Record extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", length = 20)
    private String categoryName;

    @Column(length = 200)
    private String note;

    @Column(name = "record_date", nullable = false)
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
