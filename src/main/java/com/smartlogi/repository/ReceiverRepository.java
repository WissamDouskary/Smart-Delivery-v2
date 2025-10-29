package com.smartlogi.repository;

import com.smartlogi.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiverRepository extends JpaRepository<Receiver, String> {
}
