package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.requestsDTO.ProductRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ProductResponseDTO;
import com.smartlogi.delivery.model.Products;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Products toEntity(ProductRequestDTO dto);
    ProductResponseDTO toDTO(Products products);
    List<ProductResponseDTO> toListDTO(List<Products> products);
}
