package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.dto.responseDTO.LivraisonStatsDTO;
import com.smartlogi.delivery.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String> {
    List<Colis> findColisBySender_Id(String senderId);
    List<Colis> findColisByReceiver_Id(String receiverId);
    List<Colis> findColisByLivreur_Id(String livreurId);
    List<Colis> findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(
            String descKeyword,
            String villeKeyword
    );

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
