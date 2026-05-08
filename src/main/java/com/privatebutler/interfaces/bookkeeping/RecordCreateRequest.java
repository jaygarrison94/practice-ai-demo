package com.privatebutler.interfaces.bookkeeping;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordCreateRequest {
    @NotNull(message = "请选择收支类型")
    private Integer type;

    @NotNull(message = "请填写金额")
    @DecimalMin(value = "0.01", message = "金额需大于0")
    @DecimalMax(value = "999999.99", message = "金额不能超过999999.99")
    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private String note;

    private LocalDate recordDate;
}
