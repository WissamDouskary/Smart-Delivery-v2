package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.service.SenderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sender")
public class SenderController {
    private SenderService senderService;

    public SenderController(SenderService senderService) {
        this.senderService = senderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SenderResponseDTO>> saveSender(@Valid @RequestBody SenderRequestDTO dto) {
        SenderResponseDTO responseDTO = senderService.saveSender(dto);

        ApiResponse<SenderResponseDTO> apiResponse =
                new ApiResponse<>("Sender enregistré avec succès", responseDTO);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SenderResponseDTO>> findSenderById(@PathVariable String id) {
        SenderResponseDTO senderResponseDTO = senderService.findById(id);

        ApiResponse<SenderResponseDTO> apiResponse = new ApiResponse<>("Sender Trouvé!", senderResponseDTO);
        return ResponseEntity.ok(apiResponse);
    }
}