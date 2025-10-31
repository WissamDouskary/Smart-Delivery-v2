package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.AccessDeniedException;
import com.smartlogi.exception.OperationNotAllowedException;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.*;
import com.smartlogi.model.*;
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
    private LivreurMapper livreurMapper;

    @Autowired
    public ColisService(LivreurMapper livreurMapper, LivreurService livreurService, ZoneMapper zoneMapper, ReceiverMapper receiverMapper, SenderMapper senderMapper, ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService, SenderService senderService){
        this.colisRepository = colisRepository;
        this.cityService = cityService;
        this.senderService = senderService;
        this.colisMapper = colisMapper;
        this.receiverService = receiverService;
        this.zoneMapper = zoneMapper;
        this.receiverMapper = receiverMapper;
        this.senderMapper = senderMapper;
        this.livreurService = livreurService;
        this.livreurMapper = livreurMapper;
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

    public ColisResponseDTO updateColis(ColisResponseDTO dto, String colis_id) {
        Colis colis = colisRepository.findById(colis_id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: " + colis_id));

        Livreur livreurEntity = null;
        if (dto.getLivreur() != null && dto.getLivreur().getId() != null) {
            livreurEntity = livreurService.findEntityById(dto.getLivreur().getId());
        } else {
            livreurEntity = colis.getLivreur();
        }

        Sender senderEntity = null;
        if (dto.getSender() != null && dto.getSender().getId() != null) {
            senderEntity = senderService.findEntityById(dto.getSender().getId());
        } else {
            senderEntity = colis.getSender();
        }

        Receiver receiverEntity = null;
        if (dto.getReceiver() != null && dto.getReceiver().getId() != null) {
            receiverEntity = receiverService.findEntityById(dto.getReceiver().getId());
        } else {
            receiverEntity = colis.getReceiver();
        }

        Zone zone = null;
        if (dto.getCity() != null && dto.getCity().getId() != null) {
            ZoneResponseDTO zoneResponseDTO = cityService.findCityById(dto.getCity().getId());
            zone = zoneMapper.toEntity(zoneResponseDTO);
            if (dto.getVileDistination() != null && !dto.getVileDistination().equals(zone.getNom())) {
                throw new AccessDeniedException("La ville destinataire doit être la même que celle du city id.");
            }
        } else {
            zone = colis.getCity();
        }

        if (dto.getDescription() == null) dto.setDescription(colis.getDescription());
        if (dto.getPoids() == null || dto.getPoids() == 0) dto.setPoids(colis.getPoids());
        if (dto.getVileDistination() == null) dto.setVileDistination(zone.getNom());
        if (dto.getStatus() == null) dto.setStatus(colis.getStatus());
        if (dto.getPriority() == null) dto.setPriority(colis.getPriority());

        colis.setDescription(dto.getDescription());
        colis.setPoids(dto.getPoids());
        colis.setVileDistination(zone.getNom());
        colis.setStatus(dto.getStatus());
        colis.setPriority(dto.getPriority());
        colis.setSender(senderEntity);
        colis.setReceiver(receiverEntity);
        colis.setLivreur(livreurEntity);
        colis.setCity(zone);

        Colis updated = colisRepository.save(colis);
        return colisMapper.toDTO(updated);
    }

    public void deleteColis(String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        colisRepository.delete(colis);
    }

    public ColisResponseDTO affectColisToLivreur(String livreur_id, String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        Livreur livreur = livreurService.findEntityById(livreur_id);

        if(colis.getLivreur() != null && livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("livreur est deja affecter sur ce Colis");
        }

        if(livreur.getCity().equals(colis.getCity())){
            throw new OperationNotAllowedException("livreur ville est different de colis ville!");
        }

        colis.setLivreur(livreur);

        Colis saved = colisRepository.save(colis);

        return colisMapper.toDTO(saved);
    }

    public ColisResponseDTO updateColisByLivreur(String livreur_id, Status status, String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        Livreur livreur = livreurService.findEntityById(livreur_id);

        if(!livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("You can't change statut for colis not assigned to you!");
        }
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
