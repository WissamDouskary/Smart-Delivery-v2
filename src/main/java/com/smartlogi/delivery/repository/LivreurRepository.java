package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.model.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, String> {
    List<Livreur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(
            String nom,
            String prenom,
            String telephone,
            String city
    );
    
    Optional<Livreur> findLivreurByEmail(String email);
}
