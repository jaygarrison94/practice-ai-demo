package com.privatebutler.domain.bookkeeping.repository;

import com.privatebutler.domain.common.BaseRepository;
import com.privatebutler.domain.bookkeeping.entity.CustomCategory;

import java.util.List;
import java.util.Optional;

public interface CustomCategoryRepository extends BaseRepository<CustomCategory> {
    List<CustomCategory> findByUserIdAndStatus(Long userId, Integer status);
    Optional<CustomCategory> findByUserIdAndNameAndStatus(Long userId, String name, Integer status);
    long countByUserIdAndTypeAndStatus(Long userId, Integer type, Integer status);
}
