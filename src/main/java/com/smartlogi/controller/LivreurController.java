package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.model.Livreur;
import com.smartlogi.service.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livreur")
@Tag(name = "Livreur Management", description = "Endpoints for managing Livreurs")
public class LivreurController {
    private LivreurService livreurService;

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
