package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.delivery.model.Livreur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivreurMapper {
    Livreur toEntity(LivreurRequestDTO livreurRequestDTO);
    LivreurResponseDTO toDTO(Livreur livreur);
}
