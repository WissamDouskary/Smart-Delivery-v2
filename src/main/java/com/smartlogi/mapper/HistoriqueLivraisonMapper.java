package com.smartlogi.mapper;

import com.smartlogi.dto.responseDTO.HistoriqueLivraisonResponseDTO;
import com.smartlogi.model.HistoriqueLivraison;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {
    HistoriqueLivraisonResponseDTO toDTO(HistoriqueLivraison historique);
    List<HistoriqueLivraisonResponseDTO> toDTOList(List<HistoriqueLivraison> historiques);
}