package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.AccessDeniedException;
import com.smartlogi.exception.OperationNotAllowedException;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mail.EmailDetails;
import com.smartlogi.mail.service.EmailService;
import com.smartlogi.mapper.*;
import com.smartlogi.model.*;
import com.smartlogi.repository.ColisRepository;
import com.smartlogi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ColisService {
    private final ColisRepository colisRepository;
    private final CityService cityService;
    private final ColisMapper colisMapper;
    private final ReceiverService receiverService;
    private final SenderService senderService;
    private final ZoneMapper zoneMapper;
    private ReceiverMapper receiverMapper;
    private final SenderMapper senderMapper;
    private final LivreurService livreurService;
    private LivreurMapper livreurMapper;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    @Autowired
    public ColisService(EmailService emailService, ProductRepository productRepository, LivreurMapper livreurMapper, LivreurService livreurService, ZoneMapper zoneMapper, ReceiverMapper receiverMapper, SenderMapper senderMapper, ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService, SenderService senderService){
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
        this.productRepository = productRepository;
        this.emailService = emailService;
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
        List<Products> products = productRepository.findAllById(dto.getProductIds());

        double total_poids = 0;
        for(Products p : products){
            total_poids += p.getPoids();
        }

        colis.setPoids(total_poids);
        colis.setReceiver(receiverEntity);
        colis.setSender(senderEntity);
        colis.setProducts(products);

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setStatus(colis.getStatus());
        historique.setComment("Colis créé par le client");
        historique.setChangementDate(Instant.now());
        historique.setColis(colis);

        colis.getHistoriqueLivraisonList().add(historique);

        Colis saved = colisRepository.save(colis);

        EmailDetails emailDetails = new EmailDetails();

        emailDetails.setSubject("📦 Your Colis Has Been Successfully Created — [Tracking ID: " + colis.getId() + "]");
        emailDetails.setMsgBody(
                "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                        + "<h2 style='color:#2c7be5;'>Colis Created Successfully!</h2>"
                        + "<p>Dear <b>" + senderEntity.getNom() + " " + senderEntity.getPrenom() + "</b>,</p>"
                        + "<p>Your colis has been successfully created on <b>" + LocalDate.now() + "</b>.</p>"
                        + "<p><b>Colis Details:</b></p>"
                        + "<ul>"
                        + "<li><b>Tracking ID:</b> " + colis.getId() + "</li>"
                        + "<li><b>Status:</b> " + colis.getStatus() + "</li>"
                        + "<li><b>Destination:</b> " + colis.getVileDistination() + "</li>"
                        + "<li><b>Receiver:</b> " + receiverEntity.getNom() + " " + receiverEntity.getPrenom() + "</li>"
                        + "<li><b>Total Weight:</b> " + colis.getPoids() + " kg</li>"
                        + "</ul>"
                        + "<p>You can track your colis status anytime in your SmartLogi dashboard.</p>"
                        + "<br><p style='color:gray;'>Thank you for trusting <b>SmartLogi</b>!</p>"
                        + "</body></html>"
        );
        emailDetails.setRecipient(colis.getSender().getEmail());
        emailService.sendMailWithHTML(emailDetails);

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

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setStatus(colis.getStatus());
        historique.setComment("Colis modifier par le Gestionnaire logistique");
        historique.setChangementDate(Instant.now());
        historique.setColis(colis);

        colis.getHistoriqueLivraisonList().add(historique);

        Colis updated = colisRepository.save(colis);
        return colisMapper.toDTO(updated);
    }

    public void deleteColis(String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        colisRepository.delete(colis);
    }

    public Page<ColisResponseDTO> findAllWithFilter(
            String status,
            String zone,
            String ville,
            String priority,
            Pageable pageable
    ){
        Status enumStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                enumStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Invalid status: " + status);
            }
        }

        Priority enumPriority = null;
        if(priority != null && !priority.isEmpty()){
            try{
                enumPriority = Priority.valueOf(priority.toUpperCase());
            }
            catch (IllegalArgumentException e){
                throw new ResourceNotFoundException("Invalid priority: "+priority);
            }
        }

        Page<Colis> page = colisRepository.findAll(pageable);

        Status finalEnumStatus = enumStatus;
        Priority finalEnumPriority = enumPriority;

        List<ColisResponseDTO> filtered = page.getContent()
                .stream()
                .filter(c -> finalEnumStatus == null || c.getStatus() == finalEnumStatus)
                .filter(c -> finalEnumPriority == null || c.getPriority() == finalEnumPriority)
                .filter(c -> ville == null || c.getVileDistination().equalsIgnoreCase(ville))
                .filter(c -> zone == null || (c.getCity() != null && c.getCity().getNom().equalsIgnoreCase(zone)))
                .map(colisMapper::toDTO)
                .toList();
        if(filtered.isEmpty()){
            throw new ResourceNotFoundException("Aucun colis, essayer de changer le filter");
        }
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public Map<String, Object> getColisSummary() {
        List<Colis> colisList = colisRepository.findAll();

        Map<String, Long> groupByZone = colisList.stream()
                .filter(c -> c.getCity() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getCity().getNom(),
                        Collectors.counting()
                ));

        Map<Status, Long> groupByStatus = colisList.stream()
                .collect(Collectors.groupingBy(
                        Colis::getStatus,
                        Collectors.counting()
                ));

        Map<Priority, Long> groupByPriority = colisList.stream()
                .collect(Collectors.groupingBy(
                        Colis::getPriority,
                        Collectors.counting()
                ));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("groupByZone", groupByZone);
        summary.put("groupByStatus", groupByStatus);
        summary.put("groupByPriority", groupByPriority);

        return summary;
    }

    public ColisResponseDTO affectColisToLivreur(String livreur_id, String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        Livreur livreur = livreurService.findEntityById(livreur_id);

        if(colis.getLivreur() != null && livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("livreur est deja affecter sur ce Colis");
        }

        if(!livreur.getCity().getId().equals(colis.getCity().getId())){
            throw new OperationNotAllowedException("livreur ville est different de colis ville!");
        }

        colis.setLivreur(livreur);

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setStatus(colis.getStatus());
        historique.setComment("Colis affect au livreur nom: "+livreur.getNom() + " "+livreur.getPrenom());
        historique.setChangementDate(Instant.now());
        historique.setColis(colis);

        colis.getHistoriqueLivraisonList().add(historique);

        Colis saved = colisRepository.save(colis);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(colis.getSender().getEmail());
        emailDetails.setSubject("🚚 Your Colis Has Been Assigned to a Delivery Agent — [Tracking ID: " + colis.getId() + "]");

        String htmlBody =
                "<html><body style='font-family: Arial, sans-serif; color: #333; background-color: #f8f9fa; padding: 20px;'>"
                        + "<div style='max-width: 600px; margin: auto; background: #fff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 25px;'>"
                        + "<h2 style='color:#34a853; text-align:center;'>🚚 Colis Assigned to Livreur</h2>"
                        + "<p>Dear <b>" + colis.getSender().getNom() + " " + colis.getSender().getPrenom() + "</b>,</p>"
                        + "<p>We are pleased to inform you that your colis has been assigned to a delivery agent for shipment.</p>"
                        + "<h3 style='color:#333;'>📦 Colis Details</h3>"
                        + "<ul>"
                        + "<li><b>Tracking ID:</b> " + colis.getId() + "</li>"
                        + "<li><b>Destination:</b> " + colis.getVileDistination() + "</li>"
                        + "<li><b>Receiver:</b> " + colis.getReceiver().getNom() + " " + colis.getReceiver().getPrenom() + "</li>"
                        + "<li><b>Status:</b> " + colis.getStatus() + "</li>"
                        + "</ul>"
                        + "<h3 style='color:#333;'>👤 Livreur Details</h3>"
                        + "<ul>"
                        + "<li><b>Name:</b> " + livreur.getNom() + " " + livreur.getPrenom() + "</li>"
                        + "<li><b>City:</b> " + livreur.getCity().getNom() + "</li>"
                        + "<li><b>Contact:</b> " + livreur.getTelephone() + "</li>"
                        + "</ul>"
                        + "<p>The delivery process will begin shortly. You’ll receive updates on each step of your colis delivery.</p>"
                        + "<br><p style='color:gray; text-align:center;'>Thank you for choosing <b>SmartLogi</b>.<br>We value your trust.</p>"
                        + "</div></body></html>";

        emailDetails.setMsgBody(htmlBody);

        emailService.sendMailWithHTML(emailDetails);

        return colisMapper.toDTO(saved);
    }

    public ColisResponseDTO updateColisByLivreur(String livreur_id, Status status, String colis_id){
        Colis colis = colisRepository.findById(colis_id).orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: "+colis_id));
        Livreur livreur = livreurService.findEntityById(livreur_id);

        if(!livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("You can't change statut for colis not assigned to you!");
        }

        if(!status.equals(colis.getStatus())){
            HistoriqueLivraison historique = new HistoriqueLivraison();
            historique.setStatus(status);
            historique.setComment("Colis update status par le par livreur :" + livreur.getNom() + " " + livreur.getPrenom() +" from status "+colis.getStatus() + " to status "+ status);
            historique.setChangementDate(Instant.now());
            historique.setColis(colis);

            colis.getHistoriqueLivraisonList().add(historique);

            EmailDetails emailDetails = new EmailDetails();

            emailDetails.setSubject("🔄 Colis Status Updated — [Tracking ID: " + colis.getId() + "]");
            emailDetails.setMsgBody(
                    "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                            + "<h2 style='color:#fbbc05;'>Colis Status Update</h2>"
                            + "<p>Dear <b>" + colis.getSender().getNom() + " " + colis.getSender().getPrenom() + "</b>,</p>"
                            + "<p>Your colis status has been updated by the delivery agent <b>" + livreur.getNom() + " " + livreur.getPrenom() + "</b>.</p>"
                            + "<p><b>Updated Status:</b> " + status + "</p>"
                            + "<p><b>Previous Status:</b> " + colis.getStatus() + "</p>"
                            + "<p>We will notify you once the colis reaches its next milestone.</p>"
                            + "<br><p style='color:gray;'>Thank you for trusting <b>SmartLogi</b>.</p>"
                            + "</body></html>"
            );
            emailDetails.setRecipient(colis.getSender().getEmail());
            emailService.sendMailWithHTML(emailDetails);
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

    public List<LivraisonStatsDTO> getLivraisonStatsParLivreurEtZone(){
        List<LivraisonStatsDTO> livraisonStatsDTOList = colisRepository.getLivraisonStatsParLivreurEtZone();

        if(livraisonStatsDTOList.isEmpty()){
            throw new ResourceNotFoundException("aucun livraison affecter a une livreur!");
        }

        return livraisonStatsDTOList;
    }

    public ColisResponseDTO getColisHistorique(String id){
        Colis colis = colisRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("aucun colis avec id: "+id));
        return colisMapper.toDTO(colis);
    }
}
