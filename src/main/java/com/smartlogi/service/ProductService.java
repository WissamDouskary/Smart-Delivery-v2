package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.mapper.ProductMapper;
import com.smartlogi.model.Products;
import com.smartlogi.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO save(ProductRequestDTO dto){
        Products products = productMapper.toEntity(dto);
        Products saved = productRepository.save(products);
        return productMapper.toDTO(saved);
    }
}
