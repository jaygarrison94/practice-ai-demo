package com.privatebutler.infrastructure.persistence.user;

import com.privatebutler.domain.user.entity.User;
import com.privatebutler.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.onCreate();
            mapper.insert(entity);
        } else {
            entity.onUpdate();
            mapper.update(entity);
        }
        return entity;
    }

    @Override
    public void delete(User entity) {
        entity.onUpdate();
        mapper.update(entity);
    }

    @Override
    public List<User> findAll() {
        return mapper.findAll();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return mapper.findByPhone(phone);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return mapper.existsByPhone(phone);
    }
}
