package com.smartlogi.dto.responseDTO;

import com.smartlogi.model.Zone;

public class LivreurResponseDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicle;
    private ZoneResponseDTO city;

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

    public ZoneResponseDTO getCity() {
        return city;
    }

    public void setCity(ZoneResponseDTO city) {
        this.city = city;
    }
}
