package com.smartlogi.delivery.repository;

import com.smartlogi.delivery.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<Zone, String> {
}
