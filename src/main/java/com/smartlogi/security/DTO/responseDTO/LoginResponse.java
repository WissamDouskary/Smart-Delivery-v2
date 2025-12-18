package com.smartlogi.security.DTO.responseDTO;

public class LoginResponse {
    private String token;
    private String userRole;

    public LoginResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}