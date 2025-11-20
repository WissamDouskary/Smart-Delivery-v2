package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, String> {
    Optional<Manager> findManagerByEmail(String email);
}
