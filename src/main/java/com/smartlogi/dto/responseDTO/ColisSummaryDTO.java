package com.smartlogi.dto.responseDTO;

import com.smartlogi.enums.Status;

public class ColisSummaryDTO {
    private SenderResponseDTO sender;
    private Status status;

    public SenderResponseDTO getSender() {
        return sender;
    }

    public void setSender(SenderResponseDTO sender) {
        this.sender = sender;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}