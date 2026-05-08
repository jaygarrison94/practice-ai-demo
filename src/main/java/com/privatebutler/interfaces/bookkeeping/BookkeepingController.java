package com.privatebutler.interfaces.bookkeeping;

import com.privatebutler.application.bookkeeping.RecordService;
import com.privatebutler.application.bookkeeping.StatisticsService;
import com.privatebutler.application.common.AuthApplicationService;
import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.service.StatisticsDomainService;
import com.privatebutler.infrastructure.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookkeeping")
@RequiredArgsConstructor
public class BookkeepingController {

    private final RecordService recordService;
    private final StatisticsService statisticsService;
    private final AuthApplicationService authApplicationService;

    @PostMapping("/record")
    public ApiResponse<RecordVO> create(HttpServletRequest request, @Valid @RequestBody RecordCreateRequest req) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Record record = recordService.create(userId, req.getType(), req.getAmount(),
            req.getCategoryId(), req.getCategoryName(), req.getNote(), req.getRecordDate());
        return ApiResponse.success("记账成功", RecordVO.from(record));
    }

    @PutMapping("/record/{id}")
    public ApiResponse<RecordVO> update(HttpServletRequest request, @PathVariable Long id,
                                        @Valid @RequestBody RecordCreateRequest req) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Record record = recordService.update(id, userId, req.getType(), req.getAmount(),
            req.getCategoryId(), req.getCategoryName(), req.getNote(), req.getRecordDate());
        return ApiResponse.success("编辑成功", RecordVO.from(record));
    }

    @DeleteMapping("/record/{id}")
    public ApiResponse<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = authApplicationService.getCurrentUserId(request);
        recordService.delete(id, userId);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/record/{id}")
    public ApiResponse<RecordVO> getById(HttpServletRequest request, @PathVariable Long id) {
        Long userId = authApplicationService.getCurrentUserId(request);
        Record record = recordService.getById(id, userId);
        return ApiResponse.success(RecordVO.from(record));
    }

    @GetMapping("/records")
    public ApiResponse<List<RecordVO>> list(HttpServletRequest request,
                                            @RequestParam(required = false) Integer type,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate) {
        Long userId = authApplicationService.getCurrentUserId(request);
        List<Record> records;
        if (type != null) {
            records = recordService.listByType(userId, type);
        } else if (startDate != null && endDate != null) {
            records = recordService.listByDateRange(userId, startDate, endDate);
        } else {
            records = recordService.listByUser(userId);
        }
        List<RecordVO> vos = records.stream().map(RecordVO::from).toList();
        return ApiResponse.success(vos);
    }

    @GetMapping("/statistics")
    public ApiResponse<StatisticsDomainService.StatisticsResult> statistics(
            HttpServletRequest request,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Long userId = authApplicationService.getCurrentUserId(request);
        StatisticsDomainService.StatisticsResult result = statisticsService.getStatistics(userId, startDate, endDate);
        return ApiResponse.success(result);
    }
}
