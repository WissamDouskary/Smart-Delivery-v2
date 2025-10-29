package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.dto.responseDTO.ReceiverResponseDTO;
import com.smartlogi.model.Receiver;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReceiverMapper {
    Receiver toEntity(ReceiverRequestDTO dto);
    ReceiverResponseDTO toResponseDTO(Receiver receiver);
    List<ReceiverResponseDTO> toResponseDTOList(List<Receiver> receivers);
}