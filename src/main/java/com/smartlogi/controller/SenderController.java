package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.service.SenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sender")
@Tag(name = "Senders Management", description = "Endpoints for managing Senders")
public class SenderController {
    private final SenderService senderService;

    public SenderController(SenderService senderService) {
        this.senderService = senderService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find Sender By ID", description = "Find Sender informations by id")
    public ResponseEntity<ApiResponse<SenderResponseDTO>> findSenderById(@PathVariable String id) {
        SenderResponseDTO senderResponseDTO = senderService.findById(id);

        ApiResponse<SenderResponseDTO> apiResponse = new ApiResponse<>("Sender Trouvé!", senderResponseDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get All Senders")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SenderResponseDTO>>> findAll(){
        return ResponseEntity.ok(new ApiResponse<>("Sender donner avec success", senderService.findAll()));
    }
}