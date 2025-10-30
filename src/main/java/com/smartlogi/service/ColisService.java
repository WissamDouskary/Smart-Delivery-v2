package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.OperationNotAllowedException;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.mapper.ReceiverMapper;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Livreur;
import com.smartlogi.model.Receiver;
import com.smartlogi.model.Sender;
import com.smartlogi.repository.ColisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private LivreurService livreurService;

    @Autowired
    public ColisService(LivreurService livreurService, ZoneMapper zoneMapper, ReceiverMapper receiverMapper, SenderMapper senderMapper, ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService, SenderService senderService){
        this.colisRepository = colisRepository;
        this.cityService = cityService;
        this.senderService = senderService;
        this.colisMapper = colisMapper;
        this.receiverService = receiverService;
        this.zoneMapper = zoneMapper;
        this.receiverMapper = receiverMapper;
        this.senderMapper = senderMapper;
        this.livreurService = livreurService;
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
        Livreur livreur = livreurService.findById(dto.getLivreur().getId());

        if(!cityDTO.getId().equals(livreur.getCity().getId())){
            throw new OperationNotAllowedException("Tu doit choisi une livreur avec le meme ville du colis!");
        }

        colis.setReceiver(receiverEntity);
        colis.setSender(senderEntity);
        colis.setLivreur(livreur);

        Colis saved = colisRepository.save(colis);

        return colisMapper.toDTO(saved);
    }

    public ColisResponseDTO updateColisByLivreur(String livreur_id, Status status, String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        Livreur livreur = livreurService.findById(livreur_id);

        if(!livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("You can't change statut for colis not assigned to you!");
        }

//        ColisResponseDTO colisDto = colisMapper.toDTO(colis);
//
//        dto.setSender(colisDto.getSender());
//        dto.setDescription(colisDto.getDescription());
//        dto.setCity(colisDto.getCity());
//        dto.setHistoriqueLivraisonList(colisDto.getHistoriqueLivraisonList());
//        dto.setId(colisDto.getId());
//        dto.setLivreur(colisDto.getLivreur());
//        dto.setPoids(colisDto.getPoids());
//        dto.setPriority(colisDto.getPriority());
//        dto.setVileDistination(colisDto.getCity().getNom());
//        dto.setReceiver(colisDto.getReceiver());

        colis.setStatus(status);

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

    public List<ColisResponseDTO> findAllColisForLivreurs(String livreur_id){
        List<Colis> colisList = colisRepository.findColisByLivreur_Id(livreur_id);
        if(colisList.isEmpty()){
            throw new ResourceNotFoundException("Aucun Colis pour livreur avec id: "+livreur_id);
        }
        return colisMapper.toResponseDTOList(colisList);
    }
}
