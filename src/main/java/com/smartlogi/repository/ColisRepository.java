package com.smartlogi.repository;

import com.smartlogi.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String> {
}
