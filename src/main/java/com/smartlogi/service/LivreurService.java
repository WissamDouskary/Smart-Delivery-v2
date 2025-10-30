package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.LivreurMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Livreur;
import com.smartlogi.model.Zone;
import com.smartlogi.repository.LivreurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LivreurService {
    private LivreurRepository livreurRepository;
    private LivreurMapper livreurMapper;
    private CityService cityService;
    private ZoneMapper zoneMapper;

    @Autowired
    public LivreurService(LivreurRepository livreurRepository, LivreurMapper livreurMapper, CityService cityService, ZoneMapper zoneMapper){
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
        this.cityService = cityService;
        this.zoneMapper = zoneMapper;
    }

    public LivreurResponseDTO saveLivreur(LivreurRequestDTO dto){
        Livreur livreur = livreurMapper.toEntity(dto);
        ZoneResponseDTO city = cityService.findCityById(dto.getCity().getId());
        livreur.setCity(zoneMapper.toEntity(city));
        Livreur saved = livreurRepository.save(livreur);
        return livreurMapper.toDTO(saved);
    }

    public Livreur findById(String id){
        return livreurRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livreur not found avec id: "+id));
    }
}
