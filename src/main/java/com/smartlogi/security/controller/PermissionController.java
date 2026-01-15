package com.smartlogi.security.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.model.Permission;
import com.smartlogi.security.DTO.requestDTO.PermissionRequestDTO;
import com.smartlogi.security.DTO.responseDTO.PermissionResponseDTO;
import com.smartlogi.security.DTO.responseDTO.PermissionRoleResponseDTO;
import com.smartlogi.security.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CAN_MANAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<PermissionResponseDTO>> createPermission(
            @RequestBody PermissionRequestDTO dto
    ){
        PermissionResponseDTO permissionResponseDTO = permissionService.createPermission(dto);
        return ResponseEntity.ok(
                new ApiResponse<>("Permission ajouté avec success!", permissionResponseDTO)
        );
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<PermissionResponseDTO>> updatePermission(
            @PathVariable String permissionId,
            @RequestBody PermissionRequestDTO dto
    ){
        PermissionResponseDTO updatedPermission =
                permissionService.updatePermission(dto, permissionId);

        return ResponseEntity.ok(
                new ApiResponse<>("Permission mise à jour avec succès!", updatedPermission)
        );
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable String permissionId
    ){
        permissionService.deletePermission(permissionId);

        return ResponseEntity.ok(
                new ApiResponse<>("Permission supprimée avec succès!", null)
        );
    }

    @PostMapping("/affect/{role_name}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<PermissionRoleResponseDTO>> affectPermissionsToRole(
            @RequestBody List<String> permissionIds,
            @PathVariable("role_name") String roleName
    ){
        PermissionRoleResponseDTO permissionsToRole = permissionService.affectPermissionsToRole(roleName, permissionIds);
        return ResponseEntity.ok(
                new ApiResponse<>("Permission added successfuly to " + roleName, permissionsToRole)
        );
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('CAN_MANAGE_PERMISSIONS')")
    public List<PermissionRoleResponseDTO> getAllRolesWithPermissions() {
        return permissionService.getAllRolesWithPermissions();
    }
}