package com.smartlogi.repository;

import com.smartlogi.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String> {
    List<Colis> findColisBySender_Id(String senderId);
    List<Colis> findColisByReceiver_Id(String receiverId);
    List<Colis> findColisByLivreur_Id(String livreurId);
}
