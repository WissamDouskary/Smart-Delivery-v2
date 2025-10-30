package com.smartlogi.dto.responseDTO;

import com.smartlogi.model.Colis;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public class ZoneResponseDTO {
    private String id;
    private String nom;
    private Integer codePostal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(Integer codePostal) {
        this.codePostal = codePostal;
    }
}
