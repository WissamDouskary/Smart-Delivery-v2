package com.smartlogi.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.repository.ColisRepository;
import com.smartlogi.delivery.repository.SenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = NONE)
class SenderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SenderRepository senderRepository;
    @Autowired
    private ColisRepository colisRepository;

    @BeforeEach
    void setUp() {
        senderRepository.deleteAll();
        colisRepository.deleteAll();
    }

    @Test
    void saveSender_ShouldPersistAndReturnSuccessResponse() throws Exception {
        SenderRequestDTO request = new SenderRequestDTO();
        request.setNom("Integration Test");
        request.setPrenom("User");
        request.setTelephone("0601010101");
        request.setAdresse("Casablanca");
        request.setEmail("integration@test.com");

        mockMvc.perform(post("/api/sender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender enregistré avec succès")))
                .andExpect(jsonPath("$.data.nom", is("Integration Test")));


        var allSenders = senderRepository.findAll();
        assert allSenders.size() == 1;
        assert allSenders.get(0).getNom().equals("Integration Test");
    }

    @Test
    void findSenderById_ShouldReturnSenderFromDB() throws Exception {
        Sender sender = new Sender();
        sender.setNom("Test Sender");
        sender.setPrenom("Ali");
        sender.setTelephone("0700000000");
        sender.setAdresse("Agadir");
        sender.setEmail("test@sender.com");
        Sender saved = senderRepository.save(sender);

        mockMvc.perform(get("/api/sender/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender Trouvé!")))
                .andExpect(jsonPath("$.data.nom", is("Test Sender")));
    }

    @Test
    void findAll_ShouldReturnListOfSendersFromDB() throws Exception {
        Sender s1 = new Sender();
        s1.setNom("Sender 1");
        s1.setPrenom("A");
        s1.setTelephone("0600000001");
        s1.setAdresse("Agadir");
        s1.setEmail("s1@test.com");

        Sender s2 = new Sender();
        s2.setNom("Sender 2");
        s2.setPrenom("B");
        s2.setTelephone("0600000002");
        s2.setAdresse("Marrakech");
        s2.setEmail("s2@test.com");

        senderRepository.saveAll(java.util.List.of(s1, s2));

        mockMvc.perform(get("/api/sender"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Sender donner avec success")))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }
}
