package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ReceiverResponseDTO;
import com.smartlogi.delivery.model.Receiver;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReceiverMapper {
    Receiver toEntity(ReceiverRequestDTO dto);
    ReceiverResponseDTO toResponseDTO(Receiver receiver);
    List<ReceiverResponseDTO> toResponseDTOList(List<Receiver> receivers);
}