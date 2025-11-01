package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.responseDTO.LivraisonStatsDTO;
import com.smartlogi.repository.ColisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatistiquesController {

    private final ColisRepository colisRepository;

    public StatistiquesController(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }

    @GetMapping("/api/statistiques/livreurs-zones")
    public ResponseEntity<ApiResponse<List<LivraisonStatsDTO>>> getStatsParLivreurEtZone() {
        List<LivraisonStatsDTO> statsDTOList = colisRepository.getLivraisonStatsParLivreurEtZone();

        ApiResponse<List<LivraisonStatsDTO>> apiResponse = new ApiResponse<>("get Stats avec succes", statsDTOList);

        return ResponseEntity.ok(apiResponse);
    }
}
