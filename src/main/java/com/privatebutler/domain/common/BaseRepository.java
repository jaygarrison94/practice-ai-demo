package com.privatebutler.domain.common;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    Optional<T> findById(Long id);
    T save(T entity);
    void delete(T entity);
    List<T> findAll();
}
