package com.smartlogi.model;

import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "colis")
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "poids", nullable = false)
    private Double poids;

    @Column(name = "vileDistination", nullable = false)
    private String vileDistination;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Sender sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private Zone city;

    @OneToMany(mappedBy = "colis")
    private List<HistoriqueLivraison> historiqueLivraisonList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToMany
    @JoinTable(
            name = "colis_products",
            joinColumns = @JoinColumn(name = "colis_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )

    private List<Products> products = new ArrayList<>();

    public Zone getCity() {
        return city;
    }

    public void setCity(Zone city) {
        this.city = city;
    }

    public List<HistoriqueLivraison> getHistoriqueLivraisonList() {
        return historiqueLivraisonList;
    }

    public void setHistoriqueLivraisonList(List<HistoriqueLivraison> historiqueLivraisonList) {
        this.historiqueLivraisonList = historiqueLivraisonList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public Livreur getLivreur() {
        return livreur;
    }

    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public String getVileDistination() {
        return vileDistination;
    }

    public void setVileDistination(String vileDistination) {
        this.vileDistination = vileDistination;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
