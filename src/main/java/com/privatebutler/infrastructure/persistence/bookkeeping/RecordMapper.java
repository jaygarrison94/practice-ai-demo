package com.privatebutler.infrastructure.persistence.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface RecordMapper {

    Optional<Record> findById(Long id);

    List<Record> findByUserIdAndStatusOrderByRecordDateDesc(@Param("userId") Long userId, @Param("status") Integer status);

    List<Record> findByUserIdAndRecordDateBetweenAndStatus(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("status") Integer status);

    List<Record> findByUserIdAndTypeAndStatus(@Param("userId") Long userId, @Param("type") Integer type, @Param("status") Integer status);

    List<Record> findByUserIdAndCategoryIdAndStatus(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("status") Integer status);

    List<Record> findAll();

    int insert(Record record);

    int update(Record record);
}
