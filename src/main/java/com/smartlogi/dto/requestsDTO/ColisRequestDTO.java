package com.smartlogi.dto.requestsDTO;

import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.model.Receiver;
import com.smartlogi.model.Sender;
import com.smartlogi.model.Zone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ColisRequestDTO {

    @NotBlank(message = "La description ne peut pas être vide")
    @Size(min = 2, max = 100, message = "La description doit contenir entre 2 et 100 caractères")
    private String description;

    @NotNull(message = "Le poids ne peut pas être vide")
    @Positive(message = "Le poids doit être un nombre positif")
    private Double poids;

    @NotNull(message = "Le receiver ne peut pas être vide")
    private Receiver receiver;

    @NotNull(message = "Le sender ne peut pas être vide")
    private Sender sender;

    @NotNull(message = "La city ne peut pas être vide")
    private Zone city;

    @NotNull(message = "Le Status ne peut pas être vide")
    private Status status;

    @NotNull(message = "La priority ne peut pas être vide")
    private Priority priority;

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

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Zone getCity() {
        return city;
    }

    public void setCity(Zone city) {
        this.city = city;
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
