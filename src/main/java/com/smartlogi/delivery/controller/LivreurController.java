package com.smartlogi.delivery.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.delivery.service.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/livreur")
@Tag(name = "Livreur Management", description = "Endpoints for managing Livreurs")
public class LivreurController {
    private final LivreurService livreurService;

    public LivreurController(LivreurService livreurService){
        this.livreurService = livreurService;
    }

    @PostMapping
    @Operation(summary = "Create a new Livreur", description = "Add a new Livreur with details")
    public ResponseEntity<ApiResponse<LivreurResponseDTO>> saveLivreur(@RequestBody LivreurRequestDTO dto){
        LivreurResponseDTO l = livreurService.saveLivreur(dto);

        ApiResponse<LivreurResponseDTO> apiResponse = new ApiResponse<>("Livreur ajouter avec succes", l);

        return ResponseEntity.ok(apiResponse);
    }
}
