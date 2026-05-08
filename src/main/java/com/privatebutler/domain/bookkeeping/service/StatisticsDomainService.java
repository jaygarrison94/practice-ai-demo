package com.privatebutler.domain.bookkeeping.service;

import com.privatebutler.domain.bookkeeping.entity.Record;
import com.privatebutler.domain.bookkeeping.valueobject.RecordType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsDomainService {

    public StatisticsResult calculate(List<Record> records) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> categorySummary = new HashMap<>();

        for (Record record : records) {
            if (record.getStatus() == 0) continue;
            if (record.getRecordType() == RecordType.INCOME) {
                totalIncome = totalIncome.add(record.getAmount());
            } else {
                totalExpense = totalExpense.add(record.getAmount());
            }
            categorySummary.merge(record.getCategoryName(), record.getAmount(), BigDecimal::add);
        }

        return new StatisticsResult(totalIncome, totalExpense, categorySummary);
    }

    public record StatisticsResult(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        Map<String, BigDecimal> categorySummary
    ) {
        public BigDecimal getBalance() {
            return totalIncome.subtract(totalExpense);
        }
    }
}
