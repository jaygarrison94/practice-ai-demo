package com.privatebutler.application.user;

import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import com.privatebutler.infrastructure.security.JwtTokenProvider;
import com.privatebutler.infrastructure.security.PasswordEncoder;
import com.privatebutler.infrastructure.security.SmsCodeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsCodeManager smsCodeManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public LoginResult login(String phone, String password, boolean rememberPassword) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new IllegalArgumentException("该手机号未注册，请先注册"));
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        if (user.isLocked()) {
            throw new IllegalArgumentException("账号已锁定，请1小时后再试");
        }
        if (!passwordEncoder.matches(password, user.getPassword().getValue())) {
            user.loginFail();
            userRepository.save(user);
            int remaining = user.getRemainingAttempts();
            if (remaining <= 0) {
                throw new IllegalArgumentException("密码错误次数过多，账号已锁定，请1小时后再试");
            }
            throw new IllegalArgumentException("密码错误，请重新输入，还有" + remaining + "次机会锁定");
        }
        user.loginSuccess();
        user.setRememberPassword(rememberPassword);
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getId());
        redisTemplate.opsForValue().set("token:" + user.getId(), token, 3, TimeUnit.DAYS);
        return new LoginResult(user.getId(), token, rememberPassword ? phone : null,
            rememberPassword ? password : null);
    }

    @Transactional
    public void resetPassword(String phone, String smsCode, String newPassword, String confirmPassword) {
        userRepository.findByPhone(phone)
            .orElseThrow(() -> new IllegalArgumentException("该手机号未注册"));
        if (!smsCodeManager.validate(phone, smsCode)) {
            throw new IllegalArgumentException("验证码错误或已过期，请重新获取");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次密码不一致，请重新输入");
        }
        User user = userRepository.findByPhone(phone).get();
        user.setPassword(new com.privatebutler.domain.user.valueobject.Password(
            passwordEncoder.encode(newPassword)));
        user.setLoginFailCount(0);
        user.setLockTime(null);
        userRepository.save(user);
        smsCodeManager.invalidate(phone);
        redisTemplate.delete("token:" + user.getId());
    }

    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete("token:" + userId);
    }

    public record LoginResult(Long userId, String token, String rememberedPhone, String rememberedPassword) {}
}
