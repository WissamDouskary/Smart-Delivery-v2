package com.smartlogi.delivery.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.delivery.enums.Status;
import com.smartlogi.delivery.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colis")
@Tag(name = "Colis Management", description = "Endpoints for managing colis (packages)")
public class ColisController {

    private final ColisService colisService;

    public ColisController(ColisService colisService) {
        this.colisService = colisService;
    }

    @Operation(summary = "Create a new colis", description = "Add a new colis with receiver, sender, and details")
    @PostMapping
    @PreAuthorize("hasAuthority('CAN_CREATE_COLIS')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> saveColis(
            @Valid @RequestBody ColisRequestDTO colis) {
        ColisResponseDTO saved = colisService.saveColis(colis);
        return ResponseEntity.ok(new ApiResponse<>("Colis ajouté avec succès", saved));
    }

    @Operation(summary = "get colis by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('[CAN_READ_ALL_COLIS, CAN_READ_OWN_COLIS]')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> findColisById(@PathVariable("id") String id){
        return ResponseEntity.ok(new ApiResponse<>("Colis donner avec success", colisService.findColisById(id)));
    }

    @Operation(summary = "List all colis", description = "Retrieve all colis with optional filters and pagination")
    @GetMapping
    @PreAuthorize("hasAuthority('CAN_READ_ALL_COLIS')")
    public ResponseEntity<ApiResponse<Page<ColisResponseDTO>>> findAllColis(
            @Parameter(description = "Status of the colis") @RequestParam(required = false) String status,
            @Parameter(description = "Zone of the colis") @RequestParam(required = false) String zone,
            @Parameter(description = "Ville of the colis") @RequestParam(required = false) String ville,
            @Parameter(description = "Priority of the colis") @RequestParam(required = false) String priority,
            Pageable pageable) {

        Page<ColisResponseDTO> colisResponseDTOList = colisService.findAllWithFilter(status, zone, ville, priority, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Liste des colis récupérée avec succès", colisResponseDTOList));
    }

    @Operation(summary = "Get colis summary", description = "Fetch aggregated summary of colis by status or zone")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('CAN_VIEW_STATS')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(new ApiResponse<>("Résumé des colis récupéré avec succès", colisService.getColisSummary()));
    }

    @Operation(summary = "Get colis for a client", description = "Retrieve all colis linked to a specific client")
    @GetMapping("/client/{id}")
    @PreAuthorize("hasAuthority('CAN_READ_ALL_COLIS')")
    public ResponseEntity<ApiResponse<List<ColisResponseDTO>>> findAll() {
        return ResponseEntity.ok(new ApiResponse<>("Colis du client récupérés", colisService.findAllColisForClient()));
    }

    @Operation(summary = "Get colis for a receiver", description = "Retrieve all colis sent to a specific receiver")
    @GetMapping("/receiver/{id}")
    public ResponseEntity<ApiResponse<List<ColisSummaryDTO>>> findAllColisForReceiver() {
        return ResponseEntity.ok(new ApiResponse<>("Colis du destinataire récupérés", colisService.findAllColisForReciever()));
    }

    @Operation(summary = "Get colis for a livreur", description = "Retrieve all colis assigned to a specific livreur")
    @GetMapping("/livreur")
    @PreAuthorize("hasAuthority('CAN_READ_OWN_COLIS')")
    public ResponseEntity<ApiResponse<List<ColisResponseDTO>>> findAllColisByLivreur_Id() {
        return ResponseEntity.ok(new ApiResponse<>("Colis du livreur récupérés", colisService.findAllColisForLivreurs()));
    }

    @Operation(summary = "Update colis status", description = "Change the status of a colis assigned to a livreur")
    @PatchMapping("{colis_id}/livreur/{livreur_id}")
    @PreAuthorize("hasAuthority('CAN_UPDATE_COLIS_STATUS')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> updateColisByLivreur(
            @PathVariable("colis_id") String colis_id,
            @PathVariable("livreur_id") String livreur_id,
            @RequestBody Status status) {

        ColisResponseDTO colisResponseDTO = colisService.updateColisByLivreur(livreur_id, status, colis_id);
        return ResponseEntity.ok(new ApiResponse<>("Statut du colis mis à jour", colisResponseDTO));
    }

    @Operation(summary = "Assign colis to a livreur", description = "Link a colis to a livreur for delivery")
    @PatchMapping("/affect/{colis_id}/livreur/{livreur_id}")
    @PreAuthorize("hasAuthority('CAN_ASSIGN_LIVREUR')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> affectColisToLivreur(
            @PathVariable("colis_id") String colis_id,
            @PathVariable("livreur_id") String livreur_id) {

        ColisResponseDTO colisResponseDTO = colisService.affectColisToLivreur(livreur_id, colis_id);
        return ResponseEntity.ok(new ApiResponse<>("Colis affecté au livreur avec succès", colisResponseDTO));
    }

    @Operation(summary = "Update a colis", description = "Update details of an existing colis")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_UPDATE_COLIS_FULL')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> updateColis(
            @PathVariable("id") String colis_id,
            @Valid @RequestBody ColisUpdateDTO colis) {
        ColisResponseDTO colisResponseDTO = colisService.updateColis(colis, colis_id);
        return ResponseEntity.ok(new ApiResponse<>("Colis mise à jour", colisResponseDTO));
    }

    @Operation(summary = "Delete a colis", description = "Remove a colis from the system by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_DELETE_COLIS')")
    public ResponseEntity<ApiResponse> deleteColis(@PathVariable("id") String colisId) {
        colisService.deleteColis(colisId);
        return ResponseEntity.ok(new ApiResponse<>("Colis supprimé avec succès", null));
    }

    @Operation(summary = "Get colis history", description = "Retrieve the full history of a colis by ID")
    @GetMapping("/{id}/historique")
    @PreAuthorize("hasAnyAuthority('[CAN_READ_OWN_COLIS_HISTORIQUE, CAN_READ_COLIS_HISTORIQUE_FULL]')")
    public ResponseEntity<ApiResponse<ColisResponseDTO>> getColisHistorique(@PathVariable String id) {
        ColisResponseDTO response = colisService.getColisHistorique(id);
        return ResponseEntity.ok(new ApiResponse<>("Historique du colis récupéré", response));
    }
}
