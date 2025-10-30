package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.model.Colis;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReceiverMapper.class, SenderMapper.class, ZoneMapper.class})
public interface ColisMapper {
    Colis toEntity(ColisRequestDTO colisRequestDTO);
    ColisResponseDTO toDTO(Colis colis);
    List<ColisResponseDTO> toResponseDTOList(List<Colis> colisList);
}