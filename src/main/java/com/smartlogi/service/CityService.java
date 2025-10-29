package com.smartlogi.service;

import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Zone;
import com.smartlogi.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CityService {
    private CityRepository cityRepository;
    private ZoneMapper zoneMapper;

    public CityService(CityRepository cityRepository, ZoneMapper zoneMapper){
        this.cityRepository = cityRepository;
        this.zoneMapper = zoneMapper;
    }

    public ZoneResponseDTO findCityById(String id){
        Zone ZoneResponseDTO = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun City avec ce id: "+id));
        return zoneMapper.toDAO(ZoneResponseDTO);
    }
}
