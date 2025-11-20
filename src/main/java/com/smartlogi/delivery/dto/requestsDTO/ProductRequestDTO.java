package com.smartlogi.delivery.dto.requestsDTO;


import jakarta.validation.constraints.NotNull;


public class ProductRequestDTO {
    @NotNull(message = "le nom de produit ne peut pas etre vide!")
    private String nom;

    @NotNull(message = "le category ne peut pas etre vide!")
    private String category;

    @NotNull(message = "le poids ne peut pas etre vide!")
    private Double poids;

    @NotNull(message = "le prix ne peut pas etre vide")
    private Double price;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
