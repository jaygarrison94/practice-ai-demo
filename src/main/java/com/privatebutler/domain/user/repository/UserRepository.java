package com.privatebutler.domain.user.repository;

import com.privatebutler.domain.common.BaseRepository;
import com.privatebutler.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
