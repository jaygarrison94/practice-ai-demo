package com.privatebutler.interfaces.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privatebutler.PrivateButlerTestApplication;
import com.privatebutler.domain.schedule.entity.RepeatRule;
import com.privatebutler.domain.schedule.entity.Schedule;
import com.privatebutler.domain.schedule.repository.ScheduleRepository;
import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import com.privatebutler.domain.user.valueobject.Password;
import com.privatebutler.domain.user.valueobject.PhoneNumber;
import com.privatebutler.infrastructure.security.PasswordEncoder;
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
import java.time.LocalTime;
import java.util.List;

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
class ScheduleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private UserRepository userRepository;

    @MockBean private StringRedisTemplate redisTemplate;

    private Long userId;

    @BeforeEach
    void setUp() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(redisTemplate.delete(anyString())).thenReturn(true);
        userId = userRepository.save(new User(
            new PhoneNumber("13800000999"), new Password(new PasswordEncoder().encode("Test1234")))).getId();
    }

    @Test
    void createSchedule_shouldPersist() throws Exception {
        var req = buildRequest("测试日程", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/schedule")
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.title").value("测试日程"));

        List<Schedule> all = scheduleRepository.findByUserIdAndStatus(userId, 1);
        assertEquals(1, all.size());
        assertEquals("测试日程", all.get(0).getTitle());
        assertNotNull(all.get(0).getId());
    }

    @Test
    void createSchedule_noTitle_shouldFail() throws Exception {
        mockMvc.perform(post("/api/schedule")
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ScheduleCreateRequest())))
            .andExpect(status().isBadRequest());

        assertEquals(0, scheduleRepository.findByUserIdAndStatus(userId, 1).size());
    }

    @Test
    void updateSchedule_shouldUpdateDb() throws Exception {
        Long scheduleId = seedSchedule("原始日程", LocalDate.now().plusDays(1));

        var req = buildRequest("更新日程", LocalDate.now().plusDays(2));

        mockMvc.perform(put("/api/schedule/" + scheduleId)
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        Schedule updated = scheduleRepository.findById(scheduleId).orElse(null);
        assertNotNull(updated);
        assertEquals("更新日程", updated.getTitle());
        assertEquals(LocalDate.now().plusDays(2), updated.getScheduleDate());
    }

    @Test
    void deleteSchedule_shouldMarkDeleted() throws Exception {
        Long scheduleId = seedSchedule("待删除日程", LocalDate.now().plusDays(1));

        mockMvc.perform(delete("/api/schedule/" + scheduleId)
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        Schedule deleted = scheduleRepository.findById(scheduleId).orElse(null);
        assertNotNull(deleted);
        assertEquals(0, deleted.getStatus().intValue());
    }

    @Test
    void getScheduleById_shouldReturn() throws Exception {
        Long scheduleId = seedSchedule("查询日程", LocalDate.now().plusDays(1));

        mockMvc.perform(get("/api/schedule/" + scheduleId)
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.title").value("查询日程"));
    }

    @Test
    void listSchedules_shouldReturnAll() throws Exception {
        seedSchedule("日程1", LocalDate.now().plusDays(1));
        seedSchedule("日程2", LocalDate.now().plusDays(2));

        mockMvc.perform(get("/api/schedule/list")
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void listByDate_shouldFilter() throws Exception {
        seedSchedule("今天日程", LocalDate.now());
        seedSchedule("明天日程", LocalDate.now().plusDays(1));

        mockMvc.perform(get("/api/schedule/list")
                .header("X-Test-UserId", userId)
                .param("date", LocalDate.now().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].title").value("今天日程"));
    }

    @Test
    void listByCategory_shouldFilter() throws Exception {
        seedScheduleWithCategory("工作会议", "WORK", LocalDate.now().plusDays(1));
        seedScheduleWithCategory("家庭聚会", "LIFE", LocalDate.now().plusDays(1));

        mockMvc.perform(get("/api/schedule/list")
                .header("X-Test-UserId", userId)
                .param("category", "WORK"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].title").value("工作会议"));
    }

    @Test
    void searchSchedules_shouldReturnMatches() throws Exception {
        seedSchedule("开会讨论", LocalDate.now().plusDays(1));
        seedSchedule("午餐聚会", LocalDate.now().plusDays(1));

        mockMvc.perform(get("/api/schedule/list")
                .header("X-Test-UserId", userId)
                .param("keyword", "开会"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].title").value("开会讨论"));
    }

    @Test
    void checkReminders_shouldReturnDue() throws Exception {
        Schedule schedule = new Schedule(userId, "待提醒日程",
            new com.privatebutler.domain.schedule.valueobject.ScheduleTime(LocalDate.now(), LocalTime.now().plusMinutes(5)),
            com.privatebutler.domain.schedule.valueobject.ReminderTime.fromValue("5m"),
            "", com.privatebutler.domain.schedule.valueobject.ScheduleCategory.OTHER,
            RepeatRule.builder().repeatType("NONE").build());
        scheduleRepository.save(schedule);

        mockMvc.perform(get("/api/schedule/reminders")
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    private Long seedSchedule(String title, LocalDate date) {
        Schedule schedule = new Schedule(userId, title,
            new com.privatebutler.domain.schedule.valueobject.ScheduleTime(date, LocalTime.now().plusHours(2)),
            com.privatebutler.domain.schedule.valueobject.ReminderTime.TEN_MINUTES,
            "", com.privatebutler.domain.schedule.valueobject.ScheduleCategory.OTHER,
            RepeatRule.builder().repeatType("NONE").build());
        return scheduleRepository.save(schedule).getId();
    }

    private Long seedScheduleWithCategory(String title, String category, LocalDate date) {
        Schedule schedule = new Schedule(userId, title,
            new com.privatebutler.domain.schedule.valueobject.ScheduleTime(date, LocalTime.now().plusHours(2)),
            com.privatebutler.domain.schedule.valueobject.ReminderTime.TEN_MINUTES,
            "", com.privatebutler.domain.schedule.valueobject.ScheduleCategory.fromString(category),
            RepeatRule.builder().repeatType("NONE").build());
        return scheduleRepository.save(schedule).getId();
    }

    private ScheduleCreateRequest buildRequest(String title, LocalDate date) {
        var req = new ScheduleCreateRequest();
        req.setTitle(title);
        req.setDate(date);
        req.setTime(LocalTime.of(10, 0));
        req.setRemindBefore("10m");
        return req;
    }
}
