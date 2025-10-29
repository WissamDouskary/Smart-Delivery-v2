package com.smartlogi.mapper;

import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.model.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    Zone toEntity(ZoneResponseDTO zoneResponseDTO);
    ZoneResponseDTO toDAO(Zone zone);
}
