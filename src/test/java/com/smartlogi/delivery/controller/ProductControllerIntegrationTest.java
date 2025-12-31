package com.smartlogi.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.delivery.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.delivery.service.ProductService;
import com.smartlogi.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // safety

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponseDTO();
        productResponse.setId("prod1");
        productResponse.setNom("Produit Test");
    }

    @Test
    void saveProduct_ShouldReturnSuccessResponse() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setNom("Produit Test");
        request.setPrice(100.0);
        request.setPoids(2.5);
        request.setCategory("ELECTRONICS");

        Mockito.when(productService.save(Mockito.any()))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("produit crée avec success")))
                .andExpect(jsonPath("$.data.nom", is("Produit Test")));
    }

    @Test
    void findAll_ShouldReturnProductsList() throws Exception {
        Mockito.when(productService.findAll())
                .thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Products donner avec success")))
                .andExpect(jsonPath("$.data[0].nom", is("Produit Test")));
    }
}
