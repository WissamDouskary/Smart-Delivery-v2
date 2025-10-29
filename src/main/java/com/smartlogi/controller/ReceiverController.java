package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.dto.responseDTO.ReceiverResponseDTO;
import com.smartlogi.model.Receiver;
import com.smartlogi.model.Sender;
import com.smartlogi.service.ReceiverService;
import com.smartlogi.service.SenderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/receiver")
public class ReceiverController {
    private ReceiverService receiverService;

    public ReceiverController(ReceiverService receiverService){
        this.receiverService = receiverService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReceiverResponseDTO>> saveReceiver(@Valid @RequestBody ReceiverRequestDTO dto){
        ReceiverResponseDTO receiver = receiverService.saveReciever(dto);

        ApiResponse<ReceiverResponseDTO> apiResponse = new ApiResponse<>("receiver enregistrer avec success!", receiver);

        return ResponseEntity.ok(apiResponse);
    }
}
