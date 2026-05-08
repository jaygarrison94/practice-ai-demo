package com.privatebutler.domain.user.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
public class Password {

    private String value;

    private transient Strength strength;

    public enum Strength {
        WEAK, MEDIUM, STRONG
    }

    public Password() {
    }

    public Password(String value) {
        if (StringUtils.isBlank(value) || value.length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }
        this.value = value;
        this.strength = evaluateStrength(value);
    }

    private Strength evaluateStrength(String pwd) {
        boolean hasLetter = pwd.matches(".*[a-zA-Z].*");
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasSpecial = pwd.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        if (hasLetter && hasDigit && hasSpecial && pwd.length() >= 8) return Strength.STRONG;
        if (hasLetter && hasDigit) return Strength.MEDIUM;
        return Strength.WEAK;
    }

    public boolean matches(String rawPassword) {
        return this.value.equals(rawPassword);
    }

    @Override
    public String toString() {
        return value;
    }
}
