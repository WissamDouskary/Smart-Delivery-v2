package com.smartlogi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.model.Products;
import com.smartlogi.model.Receiver;
import com.smartlogi.model.Sender;
import com.smartlogi.model.Zone;
import com.smartlogi.service.ColisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColisController.class)
class ColisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ColisService colisService;

    @Autowired
    private ObjectMapper objectMapper;

    private ColisResponseDTO colisResponse;

    @BeforeEach
    void setUp() {
        colisResponse = new ColisResponseDTO();
    }

    @Test
    void saveColis_ShouldReturnSuccessResponse() throws Exception {
        ColisRequestDTO request = new ColisRequestDTO();
        request.setDescription("Test colis");
        request.setVileDistination("Agadir");
        request.setPriority(Priority.NORMALE);

        Receiver receiver = new Receiver();
        receiver.setId("rec1");
        request.setReceiver(receiver);

        Sender sender = new Sender();
        sender.setId("sen1");
        request.setSender(sender);

        Zone city = new Zone();
        city.setId("zone1");
        request.setCity(city);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setId("prod1");
        productDTO.setQuantity(2);

        request.setProducts(List.of(productDTO));

        Mockito.when(colisService.saveColis(any())).thenReturn(colisResponse);

        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis ajouté avec succès")));
    }

    // Test GET /api/colis/{id}
    @Test
    void findColisById_ShouldReturnColis() throws Exception {
        Mockito.when(colisService.findColisById("1")).thenReturn(colisResponse);

        mockMvc.perform(get("/api/colis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis donner avec success")));
    }

    // Test GET /api/colis (findAll with filters)
    @Test
    void findAllColis_ShouldReturnPagedResult() throws Exception {
        Mockito.when(colisService.findAllWithFilter(any(), any(), any(), any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(colisResponse)));

        mockMvc.perform(get("/api/colis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Liste des colis récupérée avec succès")));
    }

    // Test GET /api/colis/summary
    @Test
    void getSummary_ShouldReturnSummaryMap() throws Exception {
        Map<String, Object> summary = Map.of("livrés", 5, "en attente", 3);
        Mockito.when(colisService.getColisSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/colis/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Résumé des colis récupéré avec succès")))
                .andExpect(jsonPath("$.data.livrés", is(5)));
    }

    // Test GET /api/colis/client/{id}
    @Test
    void findAllColisForClient_ShouldReturnList() throws Exception {
        Mockito.when(colisService.findAllColisForClient("10")).thenReturn(List.of(colisResponse));

        mockMvc.perform(get("/api/colis/client/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis du client récupérés")));
    }

    // Test GET /api/colis/receiver/{id}
    @Test
    void findAllColisForReceiver_ShouldReturnList() throws Exception {
        ColisSummaryDTO summaryDTO = new ColisSummaryDTO();
        Mockito.when(colisService.findAllColisForReciever("22")).thenReturn(List.of(summaryDTO));

        mockMvc.perform(get("/api/colis/receiver/22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis du destinataire récupérés")));
    }

    // Test GET /api/colis/livreur/{id}
    @Test
    void findAllColisByLivreur_ShouldReturnList() throws Exception {
        Mockito.when(colisService.findAllColisForLivreurs("55")).thenReturn(List.of(colisResponse));

        mockMvc.perform(get("/api/colis/livreur/55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis du livreur récupérés")));
    }

    // Test PATCH /api/colis/{colis_id}/livreur/{livreur_id}
    @Test
    void updateColisByLivreur_ShouldReturnUpdatedColis() throws Exception {
        Mockito.when(colisService.updateColisByLivreur(any(), any(), any())).thenReturn(colisResponse);

        mockMvc.perform(patch("/api/colis/123/livreur/456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Status.CREATED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Statut du colis mis à jour")));
    }

    // Test PATCH /api/colis/affect/{colis_id}/livreur/{livreur_id}
    @Test
    void affectColisToLivreur_ShouldReturnSuccess() throws Exception {
        Mockito.when(colisService.affectColisToLivreur(any(), any())).thenReturn(colisResponse);

        mockMvc.perform(patch("/api/colis/affect/999/livreur/111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis affecté au livreur avec succès")));
    }

    // Test PUT /api/colis/{id}
    @Test
    void updateColis_ShouldReturnUpdatedResponse() throws Exception {
        ColisUpdateDTO updateDTO = new ColisUpdateDTO();
        Mockito.when(colisService.updateColis(any(), any())).thenReturn(colisResponse);

        mockMvc.perform(put("/api/colis/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis mise à jour")));
    }

    // Test DELETE /api/colis/{id}
    @Test
    void deleteColis_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/colis/88"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis supprimé avec succès")));
        Mockito.verify(colisService).deleteColis("88");
    }

    // Test GET /api/colis/{id}/historique
    @Test
    void getColisHistorique_ShouldReturnHistorique() throws Exception {
        Mockito.when(colisService.getColisHistorique("HX1")).thenReturn(colisResponse);

        mockMvc.perform(get("/api/colis/HX1/historique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Historique du colis récupéré")));
    }
}
