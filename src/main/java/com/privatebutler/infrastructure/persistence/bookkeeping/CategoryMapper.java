package com.privatebutler.infrastructure.persistence.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.CustomCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {

    Optional<CustomCategory> findById(Long id);

    List<CustomCategory> findByUserIdAndStatusOrderBySortOrderAsc(@Param("userId") Long userId, @Param("status") Integer status);

    Optional<CustomCategory> findByUserIdAndNameAndStatus(@Param("userId") Long userId, @Param("name") String name, @Param("status") Integer status);

    long countByUserIdAndTypeAndStatus(@Param("userId") Long userId, @Param("type") Integer type, @Param("status") Integer status);

    List<CustomCategory> findAll();

    int insert(CustomCategory category);

    int update(CustomCategory category);
}
