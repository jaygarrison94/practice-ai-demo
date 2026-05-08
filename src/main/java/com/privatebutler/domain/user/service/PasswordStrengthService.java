package com.privatebutler.domain.user.service;

import com.privatebutler.domain.user.valueobject.Password;
import org.springframework.stereotype.Service;

@Service
public class PasswordStrengthService {

    public Password.Strength evaluate(String password) {
        return new Password(password).getStrength();
    }

    public String getStrengthHint(Password.Strength strength) {
        return switch (strength) {
            case WEAK -> "密码强度弱，建议包含字母+数字，长度≥6位，增加特殊符号提升安全性";
            case MEDIUM -> "密码强度中等";
            case STRONG -> "密码强度强";
        };
    }
}
