package com.smartlogi.dto.responseDTO;

import java.util.List;

public class SearchResponseDTO {
    private List<ColisResponseDTO> colis;
    private List<SenderResponseDTO> senders;
    private List<ReceiverResponseDTO> receivers;
    private List<LivreurResponseDTO> livreurs;

    public List<ColisResponseDTO> getColis() {
        return colis;
    }

    public void setColis(List<ColisResponseDTO> colis) {
        this.colis = colis;
    }

    public List<SenderResponseDTO> getSenders() {
        return senders;
    }

    public void setSenders(List<SenderResponseDTO> senders) {
        this.senders = senders;
    }

    public List<ReceiverResponseDTO> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<ReceiverResponseDTO> receivers) {
        this.receivers = receivers;
    }

    public List<LivreurResponseDTO> getLivreurs() {
        return livreurs;
    }

    public void setLivreurs(List<LivreurResponseDTO> livreurs) {
        this.livreurs = livreurs;
    }
}