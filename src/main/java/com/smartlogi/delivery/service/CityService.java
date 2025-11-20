package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.ZoneMapper;
import com.smartlogi.delivery.model.Zone;
import com.smartlogi.delivery.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final ZoneMapper zoneMapper;

    public CityService(CityRepository cityRepository, ZoneMapper zoneMapper){
        this.cityRepository = cityRepository;
        this.zoneMapper = zoneMapper;
    }

    public ZoneResponseDTO findCityById(String id){
        Zone ZoneResponseDTO = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun City avec ce id: "+id));
        return zoneMapper.toDAO(ZoneResponseDTO);
    }

    public List<ZoneResponseDTO> findAll(){
        List<Zone> zones = cityRepository.findAll();
        if(zones.isEmpty()){
            throw new ResourceNotFoundException("aucun zones!");
        }
        return zoneMapper.toListDTO(zones);
    }
}
