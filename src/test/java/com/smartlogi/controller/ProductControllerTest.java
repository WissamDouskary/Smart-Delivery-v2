package com.smartlogi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.service.ProductService;
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

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponseDTO();
        productResponse.setId("prod1");
        productResponse.setNom("Laptop");
        productResponse.setPoids(800.0);
        productResponse.setCategory("category1");
        productResponse.setPrice(10000.0);
    }

    // Test POST /api/product
    @Test
    void saveProduct_ShouldReturnSuccessResponse() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setNom("Laptop");
        request.setPoids(800.0);
        request.setCategory("category1");
        request.setPrice(10000.0);

        Mockito.when(productService.save(any())).thenReturn(productResponse);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("produit crée avec success")))
                .andExpect(jsonPath("$.data.nom", is("Laptop")))
                .andExpect(jsonPath("$.data.price", is(10000.0)));
    }

    // Test GET /api/product
    @Test
    void findAll_ShouldReturnListOfProducts() throws Exception {
        Mockito.when(productService.findAll()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Products donner avec success")))
                .andExpect(jsonPath("$.data[0].nom", is("Laptop")))
                .andExpect(jsonPath("$.data[0].price", is(10000.0)));
    }
}
