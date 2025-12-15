package com.smartlogi.delivery.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.service.SenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    public ResponseEntity<ApiResponse<SenderResponseDTO>> createSender(@RequestBody SenderRequestDTO dto){
        SenderResponseDTO sender = senderService.saveSender(dto);

        ApiResponse<SenderResponseDTO> apiResponse = new ApiResponse<>("Sender Created", sender);

        return ResponseEntity.ok(apiResponse);
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
    @PreAuthorize("hasAuthority('CAN_MANAGE_SENDERS')")
    public ResponseEntity<ApiResponse<List<SenderResponseDTO>>> findAll(){
        return ResponseEntity.ok(new ApiResponse<>("Sender donner avec success", senderService.findAll()));
    }
}