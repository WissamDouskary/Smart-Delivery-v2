package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.responseDTO.LivraisonStatsDTO;
import com.smartlogi.repository.ColisRepository;
import com.smartlogi.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Statistics Management", description = "Endpoints for managing Statistics")
public class StatistiquesController {

    private final ColisService colisService;

    public StatistiquesController(ColisService colisService) {
        this.colisService = colisService;
    }

    @GetMapping("/api/statistiques/livreurs-zones")
    @Operation(summary = "get Statistics", description = "get Statistics for livreur and zones")
    public ResponseEntity<ApiResponse<List<LivraisonStatsDTO>>> getStatsParLivreurEtZone() {
        List<LivraisonStatsDTO> statsDTOList = colisService.getLivraisonStatsParLivreurEtZone();

        ApiResponse<List<LivraisonStatsDTO>> apiResponse = new ApiResponse<>("get Stats avec succes", statsDTOList);

        return ResponseEntity.ok(apiResponse);
    }
}
