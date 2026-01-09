package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.dto.responseDTO.LivraisonStatsDTO;
import com.smartlogi.delivery.model.Colis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String> {
    List<Colis> findColisBySender_Id(String senderId);
    List<Colis> findColisByReceiver_Id(String receiverId);
    List<Colis> findColisByLivreur_Id(String livreurId);

    Page<Colis> findBySender_Email(String email, Pageable pageable);

    Page<Colis> findByLivreur_Email(String email, Pageable pageable);

    List<Colis> findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(
            String descKeyword,
            String villeKeyword
    );

    Optional<Colis> findColisByIdAndLivreur_Id(String id, String livreurId);
    Optional<Colis> findColisByIdAndSender_Id(String id, String senderId);

    @Query("""
        SELECT new com.smartlogi.delivery.dto.responseDTO.LivraisonStatsDTO(
            l.nom, z.nom, COUNT(c), SUM(c.poids)
        )
        FROM Colis c
        JOIN c.livreur l
        JOIN c.city z
        GROUP BY l.nom, z.nom
    """)
    List<LivraisonStatsDTO> getLivraisonStatsParLivreurEtZone();
}
