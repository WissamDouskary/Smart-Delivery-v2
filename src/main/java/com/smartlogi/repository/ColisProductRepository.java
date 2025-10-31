package com.smartlogi.repository;

import com.smartlogi.model.ColisProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisProductRepository extends JpaRepository<ColisProduct, String> {
}
