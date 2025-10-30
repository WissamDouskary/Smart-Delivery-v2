package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.model.Livreur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivreurMapper {
    Livreur toEntity(LivreurRequestDTO livreurRequestDTO);
    LivreurResponseDTO toDTO(Livreur livreur);
}
