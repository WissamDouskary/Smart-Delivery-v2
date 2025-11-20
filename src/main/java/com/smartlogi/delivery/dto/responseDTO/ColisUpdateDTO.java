package com.smartlogi.delivery.dto.responseDTO;

import com.smartlogi.delivery.enums.Priority;
import com.smartlogi.delivery.enums.Status;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ColisUpdateDTO {
    @Size(min = 2, max = 100, message = "La description doit être entre 2 et 100 caractères.")
    private String description;

    @Positive(message = "le poids doit etre positive!")
    private Double poids;

    private String receiverId;
    private String senderId;
    private String livreurId;
    private String cityId;

    private List<String> productsIds;

    private Status status;
    private Priority priority;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPoids() { return poids; }
    public void setPoids(Double poids) { this.poids = poids; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getLivreurId() { return livreurId; }
    public void setLivreurId(String livreurId) { this.livreurId = livreurId; }

    public String getCityId() { return cityId; }
    public void setCityId(String cityId) { this.cityId = cityId; }

    public List<String> getProductsIds() { return productsIds; }
    public void setProductsIds(List<String> productsIds) { this.productsIds = productsIds; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
