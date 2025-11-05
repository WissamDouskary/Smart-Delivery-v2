package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Livreur;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ReceiverMapper.class, SenderMapper.class, ZoneMapper.class, ProductMapper.class, LivreurMapper.class, HistoriqueLivraisonMapper.class}
)
public interface ColisMapper {
    Colis toEntity(ColisRequestDTO colisRequestDTO);

    ColisResponseDTO toDTO(Colis colis);
    List<ColisResponseDTO> toResponseDTOList(List<Colis> colisList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateColisFromDto(ColisUpdateDTO dto, @MappingTarget Colis colis);

}