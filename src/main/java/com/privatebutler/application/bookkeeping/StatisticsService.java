package com.privatebutler.application.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.repository.RecordRepository;
import com.privatebutler.domain.bookkeeping.service.StatisticsDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final RecordRepository recordRepository;
    private final StatisticsDomainService statisticsDomainService;

    public StatisticsDomainService.StatisticsResult getStatistics(Long userId, LocalDate start, LocalDate end) {
        List<Record> records;
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                throw new IllegalArgumentException("结束日期不能早于开始日期");
            }
            records = recordRepository.findByUserIdAndRecordDateBetweenAndStatus(userId, start, end, 1);
        } else {
            records = recordRepository.findByUserIdAndStatus(userId, 1);
        }
        if (records.isEmpty()) {
            throw new IllegalArgumentException("暂无收支记录，无法统计");
        }
        return statisticsDomainService.calculate(records);
    }
}
