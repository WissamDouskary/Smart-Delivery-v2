package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.model.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SenderRepository extends JpaRepository<Sender, String> {
    List<Sender> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom,
            String prenom,
            String email
    );

    Optional<Sender> findSenderByEmail(String email);
    boolean existsByEmail(String email);
}
