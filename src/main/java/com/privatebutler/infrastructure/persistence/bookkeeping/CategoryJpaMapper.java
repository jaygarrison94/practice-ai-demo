package com.privatebutler.infrastructure.persistence.bookkeeping;

import com.privatebutler.domain.bookkeeping.entity.CustomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaMapper extends JpaRepository<CustomCategory, Long> {

    List<CustomCategory> findByUserIdAndStatusOrderBySortOrderAsc(Long userId, Integer status);

    Optional<CustomCategory> findByUserIdAndNameAndStatus(Long userId, String name, Integer status);

    long countByUserIdAndTypeAndStatus(Long userId, Integer type, Integer status);
}
