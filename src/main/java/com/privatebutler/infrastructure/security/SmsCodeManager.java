package com.privatebutler.infrastructure.security;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SmsCodeManager {

    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "sms:code:";
    private static final String LOCK_PREFIX = "sms:lock:";
    private static final long CODE_TTL = 5;
    private static final long LOCK_TTL = 60;

    public String generateAndStore(String phone) {
        String lockKey = LOCK_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new IllegalArgumentException("验证码已发送，请60秒后再试");
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(CODE_PREFIX + phone, code, CODE_TTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(lockKey, "1", LOCK_TTL, TimeUnit.SECONDS);
        return code;
    }

    public boolean validate(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String stored = redisTemplate.opsForValue().get(key);
        if (stored == null) {
            throw new IllegalArgumentException("验证码错误或已过期，请重新获取");
        }
        boolean match = stored.equals(code);
        if (match) {
            redisTemplate.delete(key);
        }
        return match;
    }

    public void invalidate(String phone) {
        redisTemplate.delete(CODE_PREFIX + phone);
    }
}
