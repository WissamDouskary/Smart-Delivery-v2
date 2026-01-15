package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.delivery.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.delivery.exception.AccessDeniedException;
import com.smartlogi.delivery.repository.RoleRepository;
import com.smartlogi.delivery.dto.responseDTO.*;
import com.smartlogi.delivery.mapper.ColisMapper;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.mapper.ZoneMapper;
import com.smartlogi.delivery.model.*;
import com.smartlogi.delivery.repository.ColisRepository;
import com.smartlogi.delivery.repository.ProductRepository;
import com.smartlogi.delivery.repository.ReceiverRepository;
import com.smartlogi.delivery.repository.SenderRepository;
import com.smartlogi.delivery.enums.Priority;
import com.smartlogi.delivery.enums.Status;
import com.smartlogi.delivery.exception.OperationNotAllowedException;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mail.service.EmailService;
import com.smartlogi.delivery.repository.*;

import com.smartlogi.security.config.SecurityConfig;
import com.smartlogi.security.helper.AuthenticatedUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final SenderMapper senderMapper;
    private final LivreurService livreurService;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final ReceiverRepository receiverRepository;
    private final SenderRepository senderRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticatedUserHelper authenticatedUserHelper;

    @Value("${init.password}")
    private String initPassword;

    @Autowired
    public ColisService(SenderRepository senderRepository,
                        ReceiverRepository receiverRepository,
                        EmailService emailService,
                        ProductRepository productRepository,
                        LivreurService livreurService,
                        ZoneMapper zoneMapper,
                        SenderMapper senderMapper,
                        ColisRepository colisRepository,
                        CityService cityService,
                        ColisMapper colisMapper,
                        ReceiverService receiverService,
                        SenderService senderService,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        AuthenticatedUserHelper authenticatedUserHelper
    ){
        this.colisRepository = colisRepository;
        this.cityService = cityService;
        this.senderService = senderService;
        this.colisMapper = colisMapper;
        this.receiverService = receiverService;
        this.zoneMapper = zoneMapper;
        this.senderMapper = senderMapper;
        this.livreurService = livreurService;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.receiverRepository = receiverRepository;
        this.senderRepository = senderRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticatedUserHelper = authenticatedUserHelper;
    }

    @Transactional
    public ColisResponseDTO saveColis(ColisRequestDTO dto) {
        ZoneResponseDTO cityDTO = cityService.findCityById(dto.getCity().getId());

        Receiver receiverEntity;
        if (dto.getReceiver().getId() != null && !dto.getReceiver().getId().isEmpty()) {
            receiverEntity = receiverService.findEntityById(dto.getReceiver().getId());
        } else {
            receiverEntity = new Receiver();
            receiverEntity.setNom(dto.getReceiver().getNom());
            receiverEntity.setPrenom(dto.getReceiver().getPrenom());
            receiverEntity.setEmail(dto.getReceiver().getEmail());
            receiverEntity.setTelephone(dto.getReceiver().getTelephone());
            receiverEntity.setAdresse(dto.getReceiver().getAdresse());

            User user = new User();
            user.setEmail(dto.getReceiver().getEmail());
            user.setPassword(SecurityConfig.passwordEncoder().encode(initPassword));

            Role receiverRole = roleRepository.findByName("Receiver")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Receiver' not found"));

            user.setRoleEntity(receiverRole);

            user.setReceiver(receiverEntity);
            receiverEntity.setUser(user);

            receiverEntity = receiverRepository.save(receiverEntity);
        }

        User authUser = authenticatedUserHelper.getAuthenticatedUser();

        Sender senderEntity = null;

        if (authenticatedUserHelper.isSender()) {
            if (authUser.getSender() == null) {
                throw new OperationNotAllowedException("Sender account incomplete.");
            }
            senderEntity = authUser.getSender();
        }
        else if (authenticatedUserHelper.isManager()) {
            if (dto.getSender().getId() != null && !dto.getSender().getId().isEmpty()) {
                senderEntity = senderService.findEntityById(dto.getSender().getId());
            } else {
                senderEntity = new Sender();
                senderEntity.setNom(dto.getSender().getNom());
                senderEntity.setPrenom(dto.getSender().getPrenom());
                senderEntity.setEmail(dto.getSender().getEmail());
                senderEntity.setTelephone(dto.getSender().getTelephone());
                senderEntity.setAdresse(dto.getSender().getAdresse());

                User user = new User();
                user.setEmail(dto.getSender().getEmail());
                user.setPassword(SecurityConfig.passwordEncoder().encode(initPassword));

                Role senderRole = roleRepository.findByName("Sender")
                        .orElseThrow(() -> new ResourceNotFoundException("Role 'Sender' not found"));

                user.setRoleEntity(senderRole);

                user.setSender(senderEntity);
                senderEntity.setUser(user);

                senderEntity = senderRepository.save(senderEntity);
            }
        }
        else {
            throw new OperationNotAllowedException("Only Sender or Manager can create a Colis");
        }

        Colis colis = colisMapper.toEntity(dto);
        colis.setVileDistination(dto.getVileDistination());
        colis.setCity(zoneMapper.toEntity(cityDTO));
        colis.setReceiver(receiverEntity);
        colis.setSender(senderEntity);
        colis.setStatus(Status.CREATED);
        colis.setPriority(dto.getPriority());
        colis.setPoids(0.0);

        double totalPoids = 0;

        List<ColisProduct> colisProducts = new ArrayList<>();
        if (dto.getProducts() != null) {
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
            }
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
        Colis colis = null;

        if(authenticatedUserHelper.isSender()){
            colis = colisRepository.findByIdAndSender_Id(colis_id, authenticatedUserHelper.getAuthenticatedUser().getSender().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aucun colis pour ce sender ou avec id : "+colis_id));
        }else if (authenticatedUserHelper.isLivreur()){
            colis = colisRepository.findByIdAndLivreur_Id(colis_id, authenticatedUserHelper.getAuthenticatedUser().getLivreur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aucun colis pour ce livreur ou avec id: "+colis_id));
        }else{
            colis = colisRepository.findById(colis_id)
                    .orElseThrow(() -> new ResourceNotFoundException("Aucun colis avec id :" + colis_id));
        }

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
    ) {

        Status enumStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                enumStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Invalid status: " + status);
            }
        }

        Priority enumPriority = null;
        if (priority != null && !priority.isEmpty()) {
            try {
                enumPriority = Priority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Invalid priority: " + priority);
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CAN_READ_ALL_COLIS"));

        boolean isSender = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Sender"));

        boolean isLivreur = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Livreur"));

        Page<Colis> page;

        if (isAdmin) {
            page = colisRepository.findAll(pageable);
        } else if (isSender) {
            page = colisRepository.findBySender_Email(email, pageable);
        } else if (isLivreur) {
            page = colisRepository.findByLivreur_Email(email, pageable);
        } else {
            throw new AccessDeniedException("Access denied");
        }

        Status finalEnumStatus = enumStatus;
        Priority finalEnumPriority = enumPriority;

        List<ColisResponseDTO> filtered = page.getContent()
                .stream()
                .filter(c -> finalEnumStatus == null || c.getStatus() == finalEnumStatus)
                .filter(c -> finalEnumPriority == null || c.getPriority() == finalEnumPriority)
                .filter(c -> ville == null || c.getVileDistination().equalsIgnoreCase(ville))
                .filter(c -> zone == null ||
                        (c.getCity() != null && c.getCity().getNom().equalsIgnoreCase(zone)))
                .map(colisMapper::toDTO)
                .toList();

        if (filtered.isEmpty()) {
            throw new ResourceNotFoundException("Aucun colis pour ce user ou essayer de changer le filter");
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

        if(colis.getStatus() == Status.IN_STOCK && !livreur.getCity().getNom().equals("Maroc")){
            throw new OperationNotAllowedException("Colis est en stock, le livreur doit étre avec zone nom 'Maroc'!");
        }

        if(colis.getLivreur() != null && livreur.getId().equals(colis.getLivreur().getId())){
            throw new OperationNotAllowedException("livreur est deja affecter sur ce Colis");
        }

        if(!livreur.getCity().getId().equals(colis.getCity().getId()) && colis.getStatus() != Status.IN_STOCK){
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

        if(colis.getStatus() == Status.IN_STOCK){
            colis.setLivreur(null);
        }

        Colis saved = colisRepository.save(colis);

        return colisMapper.toDTO(saved);
    }

    public List<ColisResponseDTO> findAllColisForClient(){
        User user = authenticatedUserHelper.getAuthenticatedUser();

        Sender sender = user.getSender();

        if (sender == null) {
            throw new OperationNotAllowedException("You are not a Sender");
        }

        List<Colis> colisList = colisRepository.findColisBySender_Id(sender.getId());
        if(colisList.isEmpty()){
            throw new ResourceNotFoundException("Aucun Colis pour ce client");
        }
        return colisMapper.toResponseDTOList(colisList);
    }

    public List<ColisSummaryDTO> findAllColisForReciever(){
        User user = authenticatedUserHelper.getAuthenticatedUser();

        Receiver receiver = user.getReceiver();

        if (receiver == null) {
            throw new OperationNotAllowedException("You are not a livreur");
        }

        List<Colis> colisList = colisRepository.findColisByReceiver_Id(receiver.getId());
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

    public List<ColisResponseDTO> findAllColisForLivreurs(){
        User user = authenticatedUserHelper.getAuthenticatedUser();

        Livreur livreur = user.getLivreur();

        if (livreur == null) {
            throw new OperationNotAllowedException("You are not a livreur");
        }

        List<Colis> colisList = colisRepository.findColisByLivreur_Id(livreur.getId());
        if(colisList.isEmpty()){
            throw new ResourceNotFoundException("Aucun Colis pour livreur avec id: "+livreur.getId());
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

    private Status parseStatus(String status) {
        if (status == null || status.isEmpty()) return null;
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: " + status);
        }
    }

    private Priority parsePriority(String priority) {
        if (priority == null || priority.isEmpty()) return null;
        try {
            return Priority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid priority: " + priority);
        }
    }
}
