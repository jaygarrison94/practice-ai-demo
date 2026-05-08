package com.privatebutler.interfaces.bookkeeping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privatebutler.PrivateButlerTestApplication;
import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.repository.RecordRepository;
import com.privatebutler.domain.bookkeeping.valueobject.Amount;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
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

import java.math.BigDecimal;
import java.time.LocalDate;
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
class BookkeepingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RecordRepository recordRepository;
    @Autowired private UserRepository userRepository;

    @MockBean private StringRedisTemplate redisTemplate;

    private Long userId;

    @BeforeEach
    void setUp() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(redisTemplate.delete(anyString())).thenReturn(true);
        userId = userRepository.save(new User(
            new PhoneNumber("13800000998"), new Password(new PasswordEncoder().encode("Test1234")))).getId();
    }

    @Test
    void createRecord_shouldPersist() throws Exception {
        var req = buildRequest(1, new BigDecimal("99.99"), "餐饮");

        mockMvc.perform(post("/api/bookkeeping/record")
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.categoryName").value("餐饮"))
            .andExpect(jsonPath("$.data.amount").value(99.99));

        List<Record> all = recordRepository.findByUserIdAndStatus(userId, 1);
        assertEquals(1, all.size());
        assertEquals("餐饮", all.get(0).getCategoryName());
        assertEquals(0, new BigDecimal("99.99").compareTo(all.get(0).getAmount()));
        assertNotNull(all.get(0).getId());
    }

    @Test
    void createRecord_noAmount_shouldFail() throws Exception {
        var req = new RecordCreateRequest();
        req.setType(1);

        mockMvc.perform(post("/api/bookkeeping/record")
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());

        assertEquals(0, recordRepository.findByUserIdAndStatus(userId, 1).size());
    }

    @Test
    void createRecord_zeroAmount_shouldFail() throws Exception {
        var req = new RecordCreateRequest();
        req.setType(1);
        req.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/bookkeeping/record")
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());

        assertEquals(0, recordRepository.findByUserIdAndStatus(userId, 1).size());
    }

    @Test
    void updateRecord_shouldUpdateDb() throws Exception {
        Long recordId = seedRecord(1, new BigDecimal("50.00"), "交通", LocalDate.now());

        var req = buildRequest(1, new BigDecimal("80.00"), "交通（更新）");

        mockMvc.perform(put("/api/bookkeeping/record/" + recordId)
                .header("X-Test-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        Record updated = recordRepository.findById(recordId).orElse(null);
        assertNotNull(updated);
        assertEquals(0, new BigDecimal("80.00").compareTo(updated.getAmount()));
        assertEquals("交通（更新）", updated.getCategoryName());
    }

    @Test
    void deleteRecord_shouldMarkDeleted() throws Exception {
        Long recordId = seedRecord(1, new BigDecimal("30.00"), "餐饮", LocalDate.now());

        mockMvc.perform(delete("/api/bookkeeping/record/" + recordId)
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"));

        Record deleted = recordRepository.findById(recordId).orElse(null);
        assertNotNull(deleted);
        assertEquals(0, deleted.getStatus().intValue());
    }

    @Test
    void getRecordById_shouldReturn() throws Exception {
        Long recordId = seedRecord(1, new BigDecimal("15.50"), "餐饮", LocalDate.now());

        mockMvc.perform(get("/api/bookkeeping/record/" + recordId)
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.amount").value(15.50));
    }

    @Test
    void listRecords_shouldReturnAll() throws Exception {
        seedRecord(1, new BigDecimal("10.00"), "餐饮", LocalDate.now());
        seedRecord(1, new BigDecimal("20.00"), "交通", LocalDate.now());

        mockMvc.perform(get("/api/bookkeeping/records")
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void listByType_shouldFilter() throws Exception {
        seedRecord(1, new BigDecimal("10.00"), "餐饮", LocalDate.now());
        seedRecord(2, new BigDecimal("5000.00"), "工资", LocalDate.now());

        mockMvc.perform(get("/api/bookkeeping/records")
                .header("X-Test-UserId", userId)
                .param("type", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].categoryName").value("工资"));
    }

    @Test
    void listByDateRange_shouldFilter() throws Exception {
        seedRecord(1, new BigDecimal("10.00"), "餐饮", LocalDate.now().minusDays(5));
        seedRecord(1, new BigDecimal("20.00"), "交通", LocalDate.now());

        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/bookkeeping/records")
                .header("X-Test-UserId", userId)
                .param("startDate", start.toString())
                .param("endDate", end.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].categoryName").value("交通"));
    }

    @Test
    void statistics_shouldReturnStats() throws Exception {
        seedRecord(1, new BigDecimal("100.00"), "餐饮", LocalDate.now());
        seedRecord(2, new BigDecimal("5000.00"), "工资", LocalDate.now());

        mockMvc.perform(get("/api/bookkeeping/statistics")
                .header("X-Test-UserId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalExpense").value(100.00))
            .andExpect(jsonPath("$.data.totalIncome").value(5000.00));
    }

    private Long seedRecord(int type, BigDecimal amount, String category, LocalDate date) {
        Record record = new Record(userId, RecordType.fromCode(type), new Amount(amount),
            null, category, null, date);
        return recordRepository.save(record).getId();
    }

    private RecordCreateRequest buildRequest(int type, BigDecimal amount, String category) {
        var req = new RecordCreateRequest();
        req.setType(type);
        req.setAmount(amount);
        req.setCategoryName(category);
        return req;
    }
}
