package com.smartlogi.repository;

import com.smartlogi.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, String> {
    Optional<Manager> findManagerByEmail(String email);
}
