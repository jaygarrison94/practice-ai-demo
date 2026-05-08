package com.privatebutler.interfaces.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privatebutler.PrivateButlerTestApplication;
import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import com.privatebutler.infrastructure.security.SmsCodeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PrivateButlerTestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @MockBean private SmsCodeManager smsCodeManager;
    @MockBean private StringRedisTemplate redisTemplate;

    private static final String TEST_PHONE = "13800000001";
    private static final String TEST_CODE = "000000";
    private static final String TEST_PWD = "Test1234";
    private Long testUserId;

    @BeforeEach
    void setUp() {
        when(smsCodeManager.generateAndStore(anyString())).thenReturn(TEST_CODE);
        when(smsCodeManager.validate(anyString(), anyString())).thenReturn(true);
        doNothing().when(smsCodeManager).invalidate(anyString());

        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(redisTemplate.delete(anyString())).thenReturn(true);

        testUserId = null;
    }

    @Test
    void sendSmsCode_shouldReturnCode() throws Exception {
        mockMvc.perform(post("/api/user/sms-code").param("phone", TEST_PHONE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(TEST_CODE));
    }

    @Test
    void register_shouldPersistUser() throws Exception {
        var req = buildRegisterRequest();

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").isNotEmpty());

        User saved = userRepository.findByPhone(TEST_PHONE).orElse(null);
        assertNotNull(saved, "用户应该被保存到数据库");
        assertEquals(TEST_PHONE, saved.getPhone().getValue());
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());

        testUserId = saved.getId();
    }

    @Test
    void register_duplicatePhone_shouldFail() throws Exception {
        userRepository.save(new User(
            new com.privatebutler.domain.user.valueobject.PhoneNumber(TEST_PHONE),
            new com.privatebutler.domain.user.valueobject.Password(
                new com.privatebutler.infrastructure.security.PasswordEncoder().encode(TEST_PWD))));

        var req = buildRegisterRequest();
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void register_noAgreement_shouldFail() throws Exception {
        var req = buildRegisterRequest();
        req.setAgreementAccepted(false);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldSucceed() throws Exception {
        seedUser(TEST_PHONE, TEST_PWD);

        var req = new UserLoginRequest();
        req.setPhone(TEST_PHONE);
        req.setPassword(TEST_PWD);

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void login_wrongPassword_shouldFail() throws Exception {
        seedUser(TEST_PHONE, TEST_PWD);

        var req = new UserLoginRequest();
        req.setPhone(TEST_PHONE);
        req.setPassword("WrongPass1");

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void getProfile_shouldReturnUserData() throws Exception {
        User user = seedUser(TEST_PHONE, TEST_PWD);

        mockMvc.perform(get("/api/user/profile")
                .header("X-Test-UserId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.phone").value(TEST_PHONE));
    }

    @Test
    void updateProfile_shouldPersist() throws Exception {
        User user = seedUser(TEST_PHONE, TEST_PWD);
        String newNick = "新昵称";

        mockMvc.perform(put("/api/user/profile")
                .header("X-Test-UserId", user.getId())
                .param("nickname", newNick)
                .param("gender", "1")
                .param("birthday", "1990-01-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void logout_shouldSucceed() throws Exception {
        User user = seedUser(TEST_PHONE, TEST_PWD);

        mockMvc.perform(post("/api/user/logout")
                .header("X-Test-UserId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void resetPassword_shouldSucceed() throws Exception {
        seedUser(TEST_PHONE, TEST_PWD);

        mockMvc.perform(post("/api/user/reset-password")
                .param("phone", TEST_PHONE)
                .param("smsCode", TEST_CODE)
                .param("newPassword", "NewPwd123")
                .param("confirmPassword", "NewPwd123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    private UserRegisterRequest buildRegisterRequest() {
        var req = new UserRegisterRequest();
        req.setPhone(TEST_PHONE);
        req.setSmsCode(TEST_CODE);
        req.setPassword(TEST_PWD);
        req.setConfirmPassword(TEST_PWD);
        req.setAgreementAccepted(true);
        return req;
    }

    private User seedUser(String phone, String password) {
        var encoder = new com.privatebutler.infrastructure.security.PasswordEncoder();
        var user = new User(
            new com.privatebutler.domain.user.valueobject.PhoneNumber(phone),
            new com.privatebutler.domain.user.valueobject.Password(encoder.encode(password)));
        return userRepository.save(user);
    }
}
