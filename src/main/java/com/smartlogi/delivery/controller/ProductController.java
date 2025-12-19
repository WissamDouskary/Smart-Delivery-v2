package com.smartlogi.delivery.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.delivery.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Products Management", description = "Endpoints for managing Products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new Product", description = "Add a new Product with details")
    @PreAuthorize("hasAuthority('CAN_MANAGE_PRODUCTS')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> saveProduct(@Valid @RequestBody ProductRequestDTO dto){
        ProductResponseDTO saved = productService.save(dto);

        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>("produit crée avec success", saved);

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get All Products")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> findAll(){
        return ResponseEntity.ok(new ApiResponse<>("Products donner avec success", productService.findAll()));
    }
}
