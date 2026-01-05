package com.smartlogi.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.security.TestSecurityConfig;
import com.smartlogi.delivery.service.SenderService;
import com.smartlogi.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SenderController.class)
@Import(TestSecurityConfig.class)
class SenderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SenderService senderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void saveSender_ShouldReturnSuccessResponse() throws Exception {

        SenderRequestDTO request = new SenderRequestDTO();
        request.setNom("Integration Test");
        request.setPrenom("User");
        request.setTelephone("0601010101");
        request.setAdresse("Casablanca");
        request.setEmail("integration@test.com");

        SenderResponseDTO response = new SenderResponseDTO();
        response.setNom("Integration Test");

        when(senderService.saveSender(any(SenderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/sender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender Created")))
                .andExpect(jsonPath("$.data.nom", is("Integration Test")));
    }

    @Test
    void findSenderById_ShouldReturnSender() throws Exception {

        SenderResponseDTO response = new SenderResponseDTO();
        response.setNom("Test Sender");

        when(senderService.findById("1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/sender/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender Trouvé!")))
                .andExpect(jsonPath("$.data.nom", is("Test Sender")));
    }

    @Test
    void findAll_ShouldReturnListOfSenders() throws Exception {

        SenderResponseDTO s1 = new SenderResponseDTO();
        s1.setNom("Sender 1");

        SenderResponseDTO s2 = new SenderResponseDTO();
        s2.setNom("Sender 2");

        when(senderService.findAll())
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/sender"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender donner avec success")))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }
}
