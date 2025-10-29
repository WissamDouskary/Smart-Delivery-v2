package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.model.Colis;
import com.smartlogi.service.ColisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/colis")
public class ColisController {
    private ColisService colisService;

    public ColisController(ColisService colisService){
        this.colisService = colisService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ColisResponseDTO>> saveColis(@Valid @RequestBody ColisRequestDTO colis){
        ColisResponseDTO saved = colisService.saveColis(colis);
        ApiResponse<ColisResponseDTO> apiResponse = new ApiResponse<>("Colis ajouter avec succes", saved);
        return ResponseEntity.ok(apiResponse);
    }
}
