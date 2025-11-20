package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.model.Sender;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SenderMapper {
    Sender toEntity(SenderRequestDTO dto);
    SenderResponseDTO toDTO(Sender sender);
    List<SenderResponseDTO> toResponseDTOList(List<Sender> senders);
}