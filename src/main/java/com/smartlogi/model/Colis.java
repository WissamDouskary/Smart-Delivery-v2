package com.smartlogi.model;

import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colis")
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livreur_id", nullable = false)
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private Zone city;

    @OneToMany(mappedBy = "colis")
    private List<HistoriqueLivraison> historiqueLivraisonList = new ArrayList<>();

    @org.hibernate.annotations.ColumnDefault("'CREATED'")
    @Column(name = "status", columnDefinition = "order_status not null")
    private Status status;

    @Column(name = "priority", columnDefinition = "order_priority not null")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
