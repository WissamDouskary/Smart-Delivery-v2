package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.delivery.model.Colis;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ReceiverMapper.class, SenderMapper.class, ZoneMapper.class, LivreurMapper.class, HistoriqueLivraisonMapper.class, ColisProductMapper.class}
)
public interface ColisMapper {

    Colis toEntity(ColisRequestDTO colisRequestDTO);
    ColisResponseDTO toDTO(Colis colis);
    List<ColisResponseDTO> toResponseDTOList(List<Colis> colisList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateColisFromDto(ColisUpdateDTO dto, @MappingTarget Colis colis);
}
