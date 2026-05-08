package com.privatebutler.application.user;

import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileVO getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return UserProfileVO.from(user);
    }

    @Transactional
    public void updateProfile(Long userId, String nickname, String avatar,
                              Integer gender, LocalDate birthday) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (nickname != null) {
            if (nickname.length() < 1 || nickname.length() > 10) {
                throw new IllegalArgumentException("昵称长度需1-10个字符");
            }
        }
    }

    public record UserProfileVO(
        Long userId, String phone, String nickname, String avatar,
        Integer gender, LocalDate birthday,
        Boolean remindSoundEnabled, Boolean remindVibrationEnabled
    ) {
        public static UserProfileVO from(User user) {
            return new UserProfileVO(
                user.getId(),
                user.getPhone().getValue(),
                null, null, 0, null,
                true, true
            );
        }
    }
}
