package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.model.Products;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Products toEntity(ProductRequestDTO dto);
    ProductResponseDTO toDTO(Products products);
}
