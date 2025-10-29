package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Colis;
import com.smartlogi.repository.ColisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColisService {
    private ColisRepository colisRepository;
    private CityService cityService;
    private ColisMapper colisMapper;
    private ReceiverService receiverService;

    @Autowired
    public ColisService(ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService){
        this.colisRepository = colisRepository;
        this.cityService = cityService;
        this.colisMapper = colisMapper;
        this.receiverService = receiverService;
    }

    public ColisResponseDTO saveColis(ColisRequestDTO dto){
        ZoneResponseDTO city = cityService.findCityById(dto.getCity().getId());

        Colis fromDTOToEntity = colisMapper.toEntity(dto);

        fromDTOToEntity.setVileDistination(fromDTOToEntity.getCity().getNom());
        Colis saved = colisRepository.save(fromDTOToEntity);

        return colisMapper.toDTO(saved);
    }
}
