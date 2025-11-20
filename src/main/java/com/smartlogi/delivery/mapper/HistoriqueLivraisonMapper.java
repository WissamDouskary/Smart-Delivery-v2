package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.dto.responseDTO.HistoriqueLivraisonResponseDTO;
import com.smartlogi.delivery.model.HistoriqueLivraison;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {
    HistoriqueLivraisonResponseDTO toDTO(HistoriqueLivraison historique);
    List<HistoriqueLivraisonResponseDTO> toDTOList(List<HistoriqueLivraison> historiques);
}