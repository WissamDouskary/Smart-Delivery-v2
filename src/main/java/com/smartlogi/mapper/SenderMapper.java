package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.model.Sender;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SenderMapper {
    Sender toEntity(SenderRequestDTO dto);
    SenderResponseDTO toDTO(Sender sender);
    List<SenderResponseDTO> toResponseDTOList(List<Sender> senders);
}