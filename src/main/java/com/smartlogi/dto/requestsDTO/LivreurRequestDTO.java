package com.smartlogi.dto.requestsDTO;

import com.smartlogi.model.Zone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LivreurRequestDTO {
    @NotNull(message = "La description ne peut pas être vide")
    private String nom;

    @NotNull(message = "Le prenom ne peut pas être vide")
    private String prenom;

    @NotNull(message = "La telephone ne peut pas être vide")
    private String telephone;

    @Email
    private String email;

    @NotBlank(message = "le mot de pass ne peut pas étre vide")
    private String password;

    @NotNull(message = "Le vehicle ne peut pas être vide")
    private String vehicle;

    @NotNull(message = "La zone ne peut pas être vide")
    private Zone city;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public Zone getCity() {
        return city;
    }

    public void setCity(Zone city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
