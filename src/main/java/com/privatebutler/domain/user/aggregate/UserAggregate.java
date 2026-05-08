package com.privatebutler.domain.user.aggregate;

import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.valueobject.Password;
import com.privatebutler.domain.user.valueobject.PhoneNumber;
import com.privatebutler.domain.user.valueobject.UserProfile;
import lombok.Getter;

@Getter
public class UserAggregate {
    private final User user;
    private UserProfile profile;

    public UserAggregate(User user) {
        this.user = user;
    }

    public static UserAggregate register(PhoneNumber phone, Password password) {
        User user = new User(phone, password);
        return new UserAggregate(user);
    }

    public void updateProfile(UserProfile profile) {
        this.profile = profile;
    }

    public void resetPassword(Password newPassword) {
        user.setPassword(newPassword);
    }
}
