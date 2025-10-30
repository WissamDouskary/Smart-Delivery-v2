package com.smartlogi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "code_postal", nullable = false)
    private Integer codePostal;

    @OneToMany(mappedBy = "city")
    @JsonManagedReference("zone-colis")
    private List<Colis> colisList = new ArrayList<>();

    public Integer getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(Integer codePostal) {
        this.codePostal = codePostal;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Colis> getColisList() {
        return colisList;
    }

    public void setColisList(List<Colis> colisList) {
        this.colisList = colisList;
    }
}
