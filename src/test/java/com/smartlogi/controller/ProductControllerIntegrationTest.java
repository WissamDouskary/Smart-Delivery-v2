package com.smartlogi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.model.Products;
import com.smartlogi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    // Test POST /api/product
    @Test
    void saveProduct_ShouldPersistAndReturnProduct() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setNom("Laptop");
        request.setCategory("A gaming laptop");
        request.setPoids(800.0);
        request.setPrice(12000.0);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("produit crée avec success")))
                .andExpect(jsonPath("$.data.nom", is("Laptop")));

        List<Products> products = productRepository.findAll();
        assert(products.size() == 1);
        assert(products.get(0).getNom().equals("Laptop"));
    }

    // Test GET /api/product
    @Test
    void findAll_ShouldReturnAllProducts() throws Exception {
        Products p1 = new Products();
        p1.setNom("Phone");
        p1.setCategory("Smartphone");
        p1.setPrice(8000.0);
        p1.setPoids(800.0);
        productRepository.save(p1);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Products donner avec success")))
                .andExpect(jsonPath("$.data[0].nom", is("Phone")));
    }
}
