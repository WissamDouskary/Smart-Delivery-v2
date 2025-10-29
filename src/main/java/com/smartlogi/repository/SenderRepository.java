package com.smartlogi.repository;

import com.smartlogi.model.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Repository
public interface SenderRepository extends JpaRepository<Sender, String> {
}
