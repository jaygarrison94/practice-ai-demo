package com.privatebutler.domain.bookkeeping.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public class Amount {
    private final BigDecimal value;

    private static final BigDecimal MAX = new BigDecimal("999999.99");
    private static final BigDecimal MIN = BigDecimal.ZERO;

    public Amount(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("请填写金额");
        }
        if (value.compareTo(MIN) <= 0) {
            throw new IllegalArgumentException("金额需大于0");
        }
        if (value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("金额不能超过999999.99");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public Amount(String value) {
        this(new BigDecimal(value));
    }

    public Amount(double value) {
        this(BigDecimal.valueOf(value));
    }
}
