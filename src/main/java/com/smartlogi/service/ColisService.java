package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.mapper.ReceiverMapper;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Receiver;
import com.smartlogi.model.Sender;
import com.smartlogi.repository.ColisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColisService {
    private ColisRepository colisRepository;
    private CityService cityService;
    private ColisMapper colisMapper;
    private ReceiverService receiverService;
    private SenderService senderService;
    private ZoneMapper zoneMapper;
    private ReceiverMapper receiverMapper;
    private SenderMapper senderMapper;

    @Autowired
    public ColisService(ZoneMapper zoneMapper, ReceiverMapper receiverMapper, SenderMapper senderMapper, ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService, SenderService senderService){
        this.colisRepository = colisRepository;
        this.cityService = cityService;
        this.senderService = senderService;
        this.colisMapper = colisMapper;
        this.receiverService = receiverService;
        this.zoneMapper = zoneMapper;
        this.receiverMapper = receiverMapper;
        this.senderMapper = senderMapper;
    }

    public ColisResponseDTO saveColis(ColisRequestDTO dto){
        ZoneResponseDTO cityDTO = cityService.findCityById(dto.getCity().getId());
        ReceiverResponseDTO receiverDTO = receiverService.findById(dto.getReceiver().getId());
        SenderResponseDTO senderDTO = senderService.findById(dto.getSender().getId());

        Colis colis = colisMapper.toEntity(dto);

        colis.setVileDistination(cityDTO.getNom());
        colis.setCity(zoneMapper.toEntity(cityDTO));

        Receiver receiverEntity = receiverService.findEntityById(dto.getReceiver().getId());
        Sender senderEntity = senderService.findEntityById(dto.getSender().getId());

        colis.setReceiver(receiverEntity);
        colis.setSender(senderEntity);

        Colis saved = colisRepository.save(colis);

        return colisMapper.toDTO(saved);
    }

    public List<ColisResponseDTO> findAllColisForClient(String sender_id){
        List<Colis> colisList = colisRepository.findColisBySender_Id(sender_id);
        if(colisList.isEmpty()){
            throw new ResourceNotFoundException("Aucun Colis pour ce client");
        }
        return colisMapper.toResponseDTOList(colisList);
    }

    public List<ColisSummaryDTO> findAllColisForReciever(String reciever_id){
        List<Colis> colisList = colisRepository.findColisByReceiver_Id(reciever_id);
        if (colisList.isEmpty()) {
            throw new ResourceNotFoundException("No colis found for this receiver");
        }

        List<ColisSummaryDTO> colisResponseDTOList = colisList.stream().map(c -> {
            ColisSummaryDTO dto = new ColisSummaryDTO();
            dto.setSender(senderMapper.toResponseDTO(c.getSender()));
            dto.setStatus(c.getStatus());
            return dto;
        }).toList();

        return colisResponseDTOList;
    }
}
