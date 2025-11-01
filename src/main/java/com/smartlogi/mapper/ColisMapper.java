package com.smartlogi.mapper;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Livreur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ReceiverMapper.class, SenderMapper.class, ZoneMapper.class, ProductMapper.class, LivreurMapper.class, HistoriqueLivraisonMapper.class}
)
public interface ColisMapper {
    Colis toEntity(ColisRequestDTO colisRequestDTO);

    @Mapping(target = "productsList", source = "products")
    ColisResponseDTO toDTO(Colis colis);
    List<ColisResponseDTO> toResponseDTOList(List<Colis> colisList);
}