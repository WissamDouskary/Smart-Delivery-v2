package com.smartlogi.repository;

import com.smartlogi.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String> {
    List<Colis> findColisBySender_Id(String senderId);
}
