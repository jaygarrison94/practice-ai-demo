package com.privatebutler.interfaces.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserProfileVO {
    private Long userId;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private Boolean remindSoundEnabled;
    private Boolean remindVibrationEnabled;
}
