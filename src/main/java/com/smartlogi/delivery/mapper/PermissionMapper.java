package com.smartlogi.delivery.mapper;

import com.smartlogi.delivery.model.Permission;
import com.smartlogi.security.DTO.requestDTO.PermissionRequestDTO;
import com.smartlogi.security.DTO.responseDTO.PermissionResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionRequestDTO dto);
    PermissionResponseDTO toResponse(Permission p);
    PermissionResponseDTO toDto(Permission p);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePermissionFromDto(PermissionRequestDTO dto, @MappingTarget Permission permission);
}
