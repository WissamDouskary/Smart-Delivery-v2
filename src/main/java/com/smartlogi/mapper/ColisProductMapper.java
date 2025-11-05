package com.smartlogi.mapper;

import com.smartlogi.dto.responseDTO.ColisProductResponseDTO;
import com.smartlogi.model.ColisProduct;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ColisProductMapper {

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "nom", source = "product.nom")
    @Mapping(target = "category", source = "product.category")
    @Mapping(target = "poids", source = "product.poids")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "quantity", source = "quantity")
    ColisProductResponseDTO toDTO(ColisProduct colisProduct);

    List<ColisProductResponseDTO> toDTOList(List<ColisProduct> colisProducts);
}