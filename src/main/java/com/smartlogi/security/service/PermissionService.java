package com.smartlogi.security.service;

import com.smartlogi.delivery.exception.OperationNotAllowedException;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.PermissionMapper;
import com.smartlogi.delivery.model.Permission;
import com.smartlogi.delivery.model.Role;
import com.smartlogi.delivery.repository.RoleRepository;
import com.smartlogi.security.DTO.requestDTO.PermissionRequestDTO;
import com.smartlogi.security.DTO.responseDTO.PermissionResponseDTO;
import com.smartlogi.security.DTO.responseDTO.PermissionRoleResponseDTO;
import com.smartlogi.security.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;

    public PermissionService(
            PermissionRepository permissionRepository,
            PermissionMapper permissionMapper,
            RoleRepository roleRepository
    ){
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.roleRepository = roleRepository;
    }

    public PermissionResponseDTO createPermission(PermissionRequestDTO dto){
        Permission permission = permissionMapper.toEntity(dto);
        Permission saved = permissionRepository.save(permission);
        return permissionMapper.toResponse(saved);
    }

    public PermissionResponseDTO updatePermission(PermissionRequestDTO dto, String permissionId){
        Permission basePermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun permission avec id: "+permissionId));

        permissionMapper.updatePermissionFromDto(dto, basePermission);

        return permissionMapper.toResponse(basePermission);
    }

    public void deletePermission(String permissionId){
        Permission basePermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun permission avec id: "+permissionId));

        permissionRepository.delete(basePermission);
    }

    public PermissionRoleResponseDTO affectPermissionsToRole(String roleName, List<String> permissionsIds) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("aucun role avec le nom: " + roleName));

        Set<Permission> existingPermissions = role.getPermissions();

        for (String pId : permissionsIds) {
            Permission permission = permissionRepository.findById(pId)
                    .orElseThrow(() -> new ResourceNotFoundException("aucun permission avec id: " + pId));

            if (!existingPermissions.contains(permission)) {
                existingPermissions.add(permission);
                permission.getRoles().add(role);
            }else{
                throw new OperationNotAllowedException("cette permission est déja effectué a ce role!");
            }
        }

        role.setPermissions(existingPermissions);

        roleRepository.save(role);

        Set<PermissionResponseDTO> permissionResponseDTOs = existingPermissions.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toSet());

        PermissionRoleResponseDTO permissionRoleResponseDTO = new PermissionRoleResponseDTO();
        permissionRoleResponseDTO.setPermissions(permissionResponseDTOs);
        permissionRoleResponseDTO.setRole_name(roleName);

        return permissionRoleResponseDTO;
    }

    public List<PermissionRoleResponseDTO> getAllRolesWithPermissions() {
        return roleRepository.findAll()
                .stream()
                .map(role -> {
                    PermissionRoleResponseDTO dto = new PermissionRoleResponseDTO();
                    dto.setRole_name(role.getName());
                    dto.setPermissions(
                            role.getPermissions()
                                    .stream()
                                    .map(permissionMapper::toResponse)
                                    .collect(Collectors.toSet())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
