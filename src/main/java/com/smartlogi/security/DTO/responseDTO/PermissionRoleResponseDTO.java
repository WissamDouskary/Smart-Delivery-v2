package com.smartlogi.security.DTO.responseDTO;

import com.smartlogi.delivery.model.Permission;

import java.util.Set;

public class PermissionRoleResponseDTO {
    private String role_name;
    private Set<PermissionResponseDTO> permissions;

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public Set<PermissionResponseDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionResponseDTO> permissions) {
        this.permissions = permissions;
    }
}
