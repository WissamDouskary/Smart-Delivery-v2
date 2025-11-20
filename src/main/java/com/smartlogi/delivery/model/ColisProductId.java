package com.smartlogi.delivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ColisProductId implements Serializable {
    private static final long serialVersionUID = 2045429351406840464L;
    @Size(max = 36)
    @NotNull
    @Column(name = "colis_id", nullable = false, length = 36)
    private String colisId;

    @Size(max = 36)
    @NotNull
    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    public ColisProductId() {}

    public ColisProductId(String colisId, String productId) {
        this.colisId = colisId;
        this.productId = productId;
    }

    public String getColisId() {
        return colisId;
    }

    public void setColisId(String colisId) {
        this.colisId = colisId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ColisProductId entity = (ColisProductId) o;
        return Objects.equals(this.colisId, entity.colisId) &&
                Objects.equals(this.productId, entity.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colisId, productId);
    }

}