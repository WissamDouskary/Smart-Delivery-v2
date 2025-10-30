package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.model.Colis;
import com.smartlogi.service.ColisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/client/{id}")
    public ResponseEntity<ApiResponse<List<ColisResponseDTO>>> findAll(@PathVariable("id") String sender_id){
        List<ColisResponseDTO> colisResponseDTOList = colisService.findAllColisForClient(sender_id);
        ApiResponse<List<ColisResponseDTO>> listApiResponse = new ApiResponse<>("Colis Fetched avec succes", colisResponseDTOList);
        return ResponseEntity.ok(listApiResponse);
    }

    @GetMapping("/receiver/{id}")
    public ResponseEntity<ApiResponse<List<ColisSummaryDTO>>> findAllColisForReceiver(@PathVariable("id") String receiver_id){
        List<ColisSummaryDTO> colisResponseDTOList = colisService.findAllColisForReciever(receiver_id);

        ApiResponse<List<ColisSummaryDTO>> apiResponse = new ApiResponse<>("Receiver colis donner avec succes!", colisResponseDTOList);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/livreur/{id}")
    public ResponseEntity<ApiResponse<List<ColisResponseDTO>>> findAllColisByLivreur_Id(@PathVariable("id") String livreur_id){
        List<ColisResponseDTO> colisResponseDTOList = colisService.findAllColisForLivreurs(livreur_id);

        ApiResponse<List<ColisResponseDTO>> apiResponse = new ApiResponse<>("colis recu avec succes!", colisResponseDTOList);

        return ResponseEntity.ok(apiResponse);
    }
}
