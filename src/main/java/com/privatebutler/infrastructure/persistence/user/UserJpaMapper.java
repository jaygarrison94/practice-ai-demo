package com.privatebutler.infrastructure.persistence.user;

import com.privatebutler.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaMapper extends JpaRepository<User, Long> {
    Optional<User> findByPhoneValue(String phone);
    boolean existsByPhoneValue(String phone);
}
