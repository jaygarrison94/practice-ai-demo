package com.privatebutler.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class PhoneNumber {

    private String value;

    public PhoneNumber(String value) {
        if (StringUtils.isBlank(value) || !value.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("请输入正确的手机号");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
