package com.smartlogi.security.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;

public class PermissionRequestDTO {
    @NotBlank(message = "le nom est requis!")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
