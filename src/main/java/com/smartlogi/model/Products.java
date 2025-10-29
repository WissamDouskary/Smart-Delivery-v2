package com.smartlogi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "poids", nullable = false)
    private Double poids;

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToMany(mappedBy = "products")
    private List<Colis> colisList = new ArrayList<>();

    public List<Colis> getColisProducts() {
        return colisList;
    }

    public void setColisProducts(List<Colis> colisProducts) {
        this.colisList = colisProducts;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
