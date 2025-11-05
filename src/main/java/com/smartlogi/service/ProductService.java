package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ProductMapper;
import com.smartlogi.model.Products;
import com.smartlogi.model.Zone;
import com.smartlogi.repository.ProductRepository;
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
