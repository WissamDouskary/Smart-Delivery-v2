package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);

    boolean existsByEmail(String email);
}
