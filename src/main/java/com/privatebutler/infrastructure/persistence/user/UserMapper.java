package com.privatebutler.infrastructure.persistence.user;

import com.privatebutler.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<User> findAll();

    int insert(User user);

    int update(User user);
}
