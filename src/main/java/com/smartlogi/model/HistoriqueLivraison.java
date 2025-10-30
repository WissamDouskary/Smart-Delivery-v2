package com.smartlogi.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "historic_livraison")
public class HistoriqueLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "changement_date", nullable = false)
    private Instant changementDate;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getChangementDate() {
        return changementDate;
    }

    public void setChangementDate(Instant changementDate) {
        this.changementDate = changementDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
