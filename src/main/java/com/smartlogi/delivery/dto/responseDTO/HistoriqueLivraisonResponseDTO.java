package com.smartlogi.delivery.dto.responseDTO;

import com.smartlogi.delivery.enums.Status;

import java.time.Instant;

public class HistoriqueLivraisonResponseDTO {
    private String id;
    private Status status;
    private Instant changementDate;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getChangementDate() {
        return changementDate;
    }

    public void setChangementDate(Instant changementDate) {
        this.changementDate = changementDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}