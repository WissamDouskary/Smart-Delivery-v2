package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.OperationNotAllowedException;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mail.EmailDetails;
import com.smartlogi.mail.service.EmailService;
import com.smartlogi.mapper.*;
import com.smartlogi.model.*;
import com.smartlogi.repository.ColisProductRepository;
import com.smartlogi.repository.ColisRepository;
import com.smartlogi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
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
    private final ColisProductRepository colisProductRepository;

    @Autowired
    public ColisService(ColisProductRepository colisProductRepository, EmailService emailService, ProductRepository productRepository, LivreurMapper livreurMapper, LivreurService livreurService, ZoneMapper zoneMapper, ReceiverMapper receiverMapper, SenderMapper senderMapper, ColisRepository colisRepository, CityService cityService, ColisMapper colisMapper, ReceiverService receiverService, SenderService senderService){
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
        this.colisProductRepository = colisProductRepository;
    }

    @Transactional
    public ColisResponseDTO saveColis(ColisRequestDTO dto) {
        ZoneResponseDTO cityDTO = cityService.findCityById(dto.getCity().getId());
        Receiver receiverEntity = receiverService.findEntityById(dto.getReceiver().getId());
        Sender senderEntity = senderService.findEntityById(dto.getSender().getId());

        Colis colis = colisMapper.toEntity(dto);
        colis.setVileDistination(dto.getVileDistination());
        colis.setCity(zoneMapper.toEntity(cityDTO));
        colis.setReceiver(receiverEntity);
        colis.setSender(senderEntity);
        colis.setStatus(Status.CREATED);
        colis.setPriority(dto.getPriority());
        colis.setPoids(0.0);

        double totalPoids = 0;
        double totalPrice = 0;

        List<ColisProduct> colisProducts = new ArrayList<>();
        for (ColisProductsRequestDTO prodDto : dto.getProducts()) {
            Products product;

            if (prodDto.getId() != null && !prodDto.getId().isEmpty()) {
                product = productRepository.findById(prodDto.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + prodDto.getId()));
            } else {
                product = new Products();
                product.setNom(prodDto.getNom());
                product.setCategory(prodDto.getCategory());
                product.setPoids(prodDto.getPoids());
                product.setPrice(prodDto.getPrice());
                productRepository.save(product);
            }

            ColisProduct colisProduct = new ColisProduct();
            colisProduct.setId(new ColisProductId(colis.getId(), product.getId()));
            colisProduct.setColis(colis);
            colisProduct.setProduct(product);
            colisProduct.setQuantity((int) prodDto.getQuantity());
            colisProduct.setPrix(product.getPrice() * prodDto.getQuantity());
            colisProduct.setDateAjout(Instant.now());

            colisProducts.add(colisProduct);

            totalPoids += product.getPoids() * prodDto.getQuantity();
            totalPrice += product.getPrice() * prodDto.getQuantity();
        }

        colis.setPoids(totalPoids);
        colis.setColisProducts(colisProducts);

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setStatus(colis.getStatus());
        historique.setComment("Colis créé par le client");
        historique.setChangementDate(Instant.now());
        historique.setColis(colis);
        colis.getHistoriqueLivraisonList().add(historique);

        colis = colisRepository.save(colis);

        emailService.sendColisCreatedEmail(colis);

        return colisMapper.toDTO(colis);
    }


    public ColisResponseDTO findColisById(String colis_id){
        Colis colis = colisRepository.findById(colis_id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: " + colis_id));
        return colisMapper.toDTO(colis);
    }

    public ColisResponseDTO updateColis(ColisUpdateDTO dto, String colis_id) {
        Colis colis = colisRepository.findById(colis_id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id: " + colis_id));

        if(dto.getReceiverId() != null){
            Receiver receiver = receiverService.findEntityById(dto.getReceiverId());
            colis.setReceiver(receiver);
        }

        if(dto.getSenderId() != null){
            Sender sender = senderService.findEntityById(dto.getSenderId());
            colis.setSender(sender);
        }

        if(dto.getLivreurId() != null){
            Livreur livreur = livreurService.findEntityById(dto.getLivreurId());
            colis.setLivreur(livreur);
        }

        if(dto.getCityId() != null){
            ZoneResponseDTO zone = cityService.findCityById(dto.getCityId());
            if(!zone.getNom().equals(colis.getCity().getNom())){
                throw new OperationNotAllowedException("tu ne peut pas lieé ce colis avec une zone different");
            }
            Zone zoneEntity = zoneMapper.toEntity(zone);
            colis.setCity(zoneEntity);
        }

        colisMapper.updateColisFromDto(dto, colis);

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

        emailService.sendColisAssignedEmail(colis, livreur);

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

            emailService.sendColisStatusUpdatedEmail(colis, livreur, colis.getStatus(), status);
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
            dto.setSender(senderMapper.toDTO(c.getSender()));
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
