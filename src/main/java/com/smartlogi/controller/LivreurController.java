package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.model.Livreur;
import com.smartlogi.service.LivreurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/livreur")
public class LivreurController {
    private LivreurService livreurService;

    public LivreurController(LivreurService livreurService){
        this.livreurService = livreurService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LivreurResponseDTO>> saveLivreur(@RequestBody LivreurRequestDTO dto){
        LivreurResponseDTO l = livreurService.saveLivreur(dto);

        ApiResponse<LivreurResponseDTO> apiResponse = new ApiResponse<>("Livreur ajouter avec succes", l);

        return ResponseEntity.ok(apiResponse);
    }
}
