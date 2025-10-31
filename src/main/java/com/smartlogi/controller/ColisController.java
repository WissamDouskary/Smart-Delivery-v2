package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.service.ColisService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ColisResponseDTO>>> findAllColis(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
    ){
        Page<ColisResponseDTO> colisResponseDTOList = colisService.findAllWithFilter(status, zone, ville, priority, date, pageable);

        ApiResponse<Page<ColisResponseDTO>> apiResponse = new ApiResponse<>("Tout les colis: ", colisResponseDTOList);

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

    @PatchMapping("{colis_id}/livreur/{livreur_id}")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> updateColisByLivreur(@PathVariable("colis_id") String colis_id, @PathVariable("livreur_id") String livreur_id, @RequestBody Status status){
        ColisResponseDTO colisResponseDTO = colisService.updateColisByLivreur(livreur_id, status, colis_id);

        ApiResponse<ColisResponseDTO> apiResponse = new ApiResponse<>("Colis modifier avec succes!", colisResponseDTO);

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/affect/{colis_id}/livreur/{livreur_id}")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> affectColisToLivreur(@PathVariable("colis_id") String colis_id, @PathVariable("livreur_id") String livreur_id){
        ColisResponseDTO colisResponseDTO = colisService.affectColisToLivreur(livreur_id, colis_id);

        ApiResponse<ColisResponseDTO> apiResponse = new ApiResponse<>("Colis affected to livreur ", colisResponseDTO);

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> updateColis(@PathVariable("id") String colis_id, @RequestBody ColisResponseDTO colis){
        ColisResponseDTO colisResponseDTO = colisService.updateColis(colis, colis_id);

        ApiResponse<ColisResponseDTO> apiResponse = new ApiResponse<>("Colis modifier avec succes", colisResponseDTO);

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteColis(@PathVariable("id") String colis_id){
        colisService.deleteColis(colis_id);
        return ResponseEntity.ok(new ApiResponse("Colis supprimer avec succes!", null));
    }
}
