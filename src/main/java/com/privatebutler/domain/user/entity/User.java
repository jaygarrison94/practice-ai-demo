package com.privatebutler.domain.user.entity;

import com.privatebutler.domain.common.BaseEntity;
import com.privatebutler.domain.user.valueobject.Password;
import com.privatebutler.domain.user.valueobject.PhoneNumber;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private PhoneNumber phone;

    private Password password;

    private Boolean rememberPassword = false;

    private Integer loginFailCount = 0;

    private LocalDateTime lockTime;

    private LocalDateTime lastLoginTime;

    public User(PhoneNumber phone, Password password) {
        this.phone = phone;
        this.password = password;
        this.status = 1;
    }

    public void loginSuccess() {
        this.loginFailCount = 0;
        this.lockTime = null;
        this.lastLoginTime = LocalDateTime.now();
    }

    public void loginFail() {
        this.loginFailCount = (this.loginFailCount == null ? 0 : this.loginFailCount) + 1;
        if (this.loginFailCount >= 5) {
            this.lockTime = LocalDateTime.now().plusHours(1);
        }
    }

    public boolean isLocked() {
        if (lockTime == null) return false;
        if (LocalDateTime.now().isAfter(lockTime)) {
            this.lockTime = null;
            this.loginFailCount = 0;
            return false;
        }
        return true;
    }

    public int getRemainingAttempts() {
        return 5 - (loginFailCount == null ? 0 : loginFailCount);
    }
}
