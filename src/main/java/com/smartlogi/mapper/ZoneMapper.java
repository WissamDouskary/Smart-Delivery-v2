package com.smartlogi.mapper;

import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.model.Zone;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    Zone toEntity(ZoneResponseDTO zoneResponseDTO);
    ZoneResponseDTO toDAO(Zone zone);
    List<ZoneResponseDTO> toListDTO(List<Zone> zones);
}
