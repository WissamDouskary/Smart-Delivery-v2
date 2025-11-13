package com.smartlogi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.service.SenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SenderController.class)
class SenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SenderService senderService;

    @Autowired
    private ObjectMapper objectMapper;

    private SenderResponseDTO senderResponseDTO;

    @BeforeEach
    void setUp() {
        senderResponseDTO = new SenderResponseDTO();
        senderResponseDTO.setId("1");
        senderResponseDTO.setNom("John Doe");
        senderResponseDTO.setTelephone("0600000000");
        senderResponseDTO.setAdresse("Agadir");
    }

    @Test
    void saveSender_ShouldReturnSuccessResponse() throws Exception {
        SenderRequestDTO requestDTO = new SenderRequestDTO();
        requestDTO.setNom("John Doe");
        requestDTO.setTelephone("0600000000");
        requestDTO.setAdresse("Agadir");
        requestDTO.setEmail("john@gmail.com");
        requestDTO.setPrenom("mohmad");

        Mockito.when(senderService.saveSender(any(SenderRequestDTO.class)))
                .thenReturn(senderResponseDTO);

        mockMvc.perform(post("/api/sender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender enregistré avec succès")))
                .andExpect(jsonPath("$.data.nom", is("John Doe")));
    }

    @Test
    void findSenderById_ShouldReturnSender() throws Exception {
        Mockito.when(senderService.findById("1")).thenReturn(senderResponseDTO);

        mockMvc.perform(get("/api/sender/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender Trouvé!")))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.nom", is("John Doe")));
    }

    @Test
    void findAll_ShouldReturnListOfSenders() throws Exception {
        Mockito.when(senderService.findAll()).thenReturn(List.of(senderResponseDTO));

        mockMvc.perform(get("/api/sender"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender donner avec success")))
                .andExpect(jsonPath("$.data[0].nom", is("John Doe")));
    }
}
