package com.privatebutler.application.user;

import com.privatebutler.domain.user.aggregate.UserAggregate;
import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import com.privatebutler.domain.user.valueobject.Password;
import com.privatebutler.domain.user.valueobject.PhoneNumber;
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
public class UserRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsCodeManager smsCodeManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public String sendSmsCode(String phone) {
        new PhoneNumber(phone);
        String code = smsCodeManager.generateAndStore(phone);
        return code;
    }

    @Transactional
    public RegisterResult register(String phone, String smsCode, String password, String confirmPassword,
                                   boolean agreementAccepted) {
        if (!agreementAccepted) {
            throw new IllegalArgumentException("请勾选用户协议与隐私政策");
        }
        PhoneNumber phoneNumber = new PhoneNumber(phone);
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("该手机号已注册，请直接登录");
        }
        if (!smsCodeManager.validate(phone, smsCode)) {
            throw new IllegalArgumentException("验证码错误或已过期，请重新获取");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次密码不一致，请重新输入");
        }
        Password pwd = new Password(password);
        Password.Strength strength = pwd.getStrength();
        if (strength == Password.Strength.WEAK) {
            throw new IllegalArgumentException("密码需包含字母+数字，长度≥6位，建议增加特殊符号提升安全性");
        }
        UserAggregate aggregate = UserAggregate.register(phoneNumber, new Password(passwordEncoder.encode(password)));
        User savedUser = userRepository.save(aggregate.getUser());
        String token = jwtTokenProvider.generateToken(savedUser.getId());
        redisTemplate.opsForValue().set("token:" + savedUser.getId(), token, 3, TimeUnit.DAYS);
        smsCodeManager.invalidate(phone);
        return new RegisterResult(savedUser.getId(), token);
    }

    public record RegisterResult(Long userId, String token) {}
}
