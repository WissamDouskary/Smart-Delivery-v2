package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.dto.responseDTO.ReceiverResponseDTO;
import com.smartlogi.service.ReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receiver")
@Tag(name = "Receivers Management", description = "Endpoints for managing Receivers")
public class ReceiverController {
    private final ReceiverService receiverService;

    public ReceiverController(ReceiverService receiverService){
        this.receiverService = receiverService;
    }

    @PostMapping
    @Operation(summary = "Create a new Receiver", description = "Add a new Receiver with details")
    public ResponseEntity<ApiResponse<ReceiverResponseDTO>> saveReceiver(@Valid @RequestBody ReceiverRequestDTO dto){
        ReceiverResponseDTO receiver = receiverService.saveReciever(dto);

        ApiResponse<ReceiverResponseDTO> apiResponse = new ApiResponse<>("receiver enregistrer avec success!", receiver);

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get All Receivers")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiverResponseDTO>>> findAll(){
        return ResponseEntity.ok(new ApiResponse<>("Receivers donner avec success", receiverService.findAll()));
    }
}
