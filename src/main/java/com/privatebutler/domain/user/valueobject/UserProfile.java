package com.privatebutler.domain.user.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private Boolean remindSoundEnabled;
    private Boolean remindVibrationEnabled;
}
