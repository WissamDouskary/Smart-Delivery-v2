package com.smartlogi.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.delivery.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.delivery.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.delivery.enums.Priority;
import com.smartlogi.delivery.enums.Status;
import com.smartlogi.delivery.model.Receiver;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.model.Zone;
import com.smartlogi.delivery.service.ColisService;
import com.smartlogi.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColisController.class)
@AutoConfigureMockMvc(addFilters = false)
class ColisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ColisService colisService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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

    @Test
    void findColisById_ShouldReturnColis() throws Exception {
        Mockito.when(colisService.findColisById("1")).thenReturn(colisResponse);

        mockMvc.perform(get("/api/colis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis donner avec success")));
    }

    @Test
    void findAllColis_ShouldReturnPagedResult() throws Exception {
        Mockito.when(colisService.findAllWithFilter(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(colisResponse)));

        mockMvc.perform(get("/api/colis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Liste des colis récupérée avec succès")));
    }

    @Test
    void getSummary_ShouldReturnSummaryMap() throws Exception {
        Mockito.when(colisService.getColisSummary())
                .thenReturn(Map.of("livrés", 5, "en attente", 3));

        mockMvc.perform(get("/api/colis/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Résumé des colis récupéré avec succès")))
                .andExpect(jsonPath("$.data.livrés", is(5)));
    }

    @Test
    void affectColisToLivreur_ShouldReturnSuccess() throws Exception {
        Mockito.when(colisService.affectColisToLivreur(any(), any()))
                .thenReturn(colisResponse);

        mockMvc.perform(patch("/api/colis/affect/999/livreur/111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Colis affecté au livreur avec succès")));
    }
}
