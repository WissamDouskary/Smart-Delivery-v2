package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.service.SenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sender")
@Tag(name = "Senders Management", description = "Endpoints for managing Senders")
public class SenderController {
    private SenderService senderService;

    public SenderController(SenderService senderService) {
        this.senderService = senderService;
    }

    @PostMapping
    @Operation(summary = "Save Sender", description = "Save sender informations")
    public ResponseEntity<ApiResponse<SenderResponseDTO>> saveSender(@Valid @RequestBody SenderRequestDTO dto) {
        SenderResponseDTO responseDTO = senderService.saveSender(dto);

        ApiResponse<SenderResponseDTO> apiResponse =
                new ApiResponse<>("Sender enregistré avec succès", responseDTO);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find Sender By ID", description = "Find Sender informations by id")
    public ResponseEntity<ApiResponse<SenderResponseDTO>> findSenderById(@PathVariable String id) {
        SenderResponseDTO senderResponseDTO = senderService.findById(id);

        ApiResponse<SenderResponseDTO> apiResponse = new ApiResponse<>("Sender Trouvé!", senderResponseDTO);
        return ResponseEntity.ok(apiResponse);
    }
}