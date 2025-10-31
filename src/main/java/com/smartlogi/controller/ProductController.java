package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.model.Products;
import com.smartlogi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> saveProduct(@Valid @RequestBody ProductRequestDTO dto){
        ProductResponseDTO saved = productService.save(dto);

        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>("produit crée avec success", saved);

        return ResponseEntity.ok(apiResponse);
    }
}
