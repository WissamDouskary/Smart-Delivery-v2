package com.smartlogi.delivery.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "colis_products")
public class ColisProduct {

    @EmbeddedId
    private ColisProductId id;

    @MapsId("colisId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "date_ajout", columnDefinition = "timestamp default now()")
    private Instant dateAjout;

    public Instant getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Instant dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public ColisProductId getId() {
        return id;
    }

    public void setId(ColisProductId id) {
        this.id = id;
    }
}
