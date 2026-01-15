package com.smartlogi.delivery.dto.responseDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ColisProductResponseDTO {
    private String id;
    private String nom;
    private String category;
    private Double poids;
    private Double price;
    @NotBlank(message = "quantity is required!")
    @Positive(message = "quantity requis positive")
    private Integer quantity;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
