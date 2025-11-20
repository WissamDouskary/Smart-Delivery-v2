package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.ProductMapper;
import com.smartlogi.delivery.model.Products;
import com.smartlogi.delivery.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ProductResponseDTO> findAll(){
        List<Products> products = productRepository.findAll();
        if(products.isEmpty()){
            throw new ResourceNotFoundException("aucun products!");
        }
        return productMapper.toListDTO(products);
    }
}
