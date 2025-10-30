package com.smartlogi.dto.responseDTO;

import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.model.*;

import java.util.ArrayList;
import java.util.List;

public class ColisResponseDTO {
    private String id;
    private String description;
    private Double poids;
    private String vileDistination;
    private ReceiverResponseDTO receiver;
    private SenderResponseDTO sender;
    private LivreurResponseDTO livreur;
    private ZoneResponseDTO city;
    private List<HistoriqueLivraison> historiqueLivraisonList = new ArrayList<>();
    private Status status;
    private Priority priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public String getVileDistination() {
        return vileDistination;
    }

    public void setVileDistination(String vileDistination) {
        this.vileDistination = vileDistination;
    }

    public ReceiverResponseDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(ReceiverResponseDTO receiver) {
        this.receiver = receiver;
    }

    public SenderResponseDTO getSender() {
        return sender;
    }

    public void setSender(SenderResponseDTO sender) {
        this.sender = sender;
    }

    public LivreurResponseDTO getLivreur() {
        return livreur;
    }

    public void setLivreur(LivreurResponseDTO livreur) {
        this.livreur = livreur;
    }

    public ZoneResponseDTO getCity() {
        return city;
    }

    public void setCity(ZoneResponseDTO city) {
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
}
