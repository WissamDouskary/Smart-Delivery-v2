package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ColisSummaryDTO;
import com.smartlogi.dto.responseDTO.ColisUpdateDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.OperationNotAllowedException;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mail.service.EmailService;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.mapper.ReceiverMapper;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.*;
import com.smartlogi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColisServiceTest {
    @Mock
    private ColisRepository colisRepository;

    @Mock
    private ColisMapper colisMapper;

    @Mock
    private ReceiverMapper receiverMapper;

    @Mock
    private SenderMapper senderMapper;

    @InjectMocks
    private ColisService colisService;

    @Mock
    private CityService cityService;

    @Mock
    private ReceiverRepository receiverRepository;
    @Mock
    private SenderRepository senderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReceiverService receiverService;
    @Mock
    private SenderService senderService;
    @Mock
    private ZoneMapper zoneMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private LivreurService livreurService;

    private Colis colis;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // find all colis with filter tests
    @Test
    void testFindAllWithFilter_ReturnsFilteredList() {
        Pageable pageable = PageRequest.of(0, 10);

        Colis colis = new Colis();
        colis.setId("c1");
        colis.setStatus(Status.CREATED);
        colis.setPriority(Priority.NORMALE);
        colis.setVileDistination("Casablanca");

        Zone city = new Zone();
        city.setNom("Agadir");
        colis.setCity(city);

        ColisResponseDTO colisDTO = new ColisResponseDTO();
        colisDTO.setId("c1");

        Page<Colis> page = new PageImpl<>(List.of(colis));

        when(colisRepository.findAll(pageable)).thenReturn(page);
        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);

        Page<ColisResponseDTO> result = colisService.findAllWithFilter(
                "CREATED", "Agadir", "Casablanca", "NORMALE", pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("c1", result.getContent().get(0).getId());

        verify(colisRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindAllWithFilter_InvalidStatus_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(ResourceNotFoundException.class, () ->
                colisService.findAllWithFilter("INVALID", null, null, null, pageable)
        );
    }

    @Test
    void testFindAllWithFilter_NoResults_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Colis> emptyPage = new PageImpl<>(List.of());
        when(colisRepository.findAll(pageable)).thenReturn(emptyPage);

        assertThrows(ResourceNotFoundException.class, () ->
                colisService.findAllWithFilter(null, null, null, null, pageable)
        );
    }

    @Test
    void testFindAllWithFilter_InvalidPriority_ThrowsException(){
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(ResourceNotFoundException.class, () ->
                colisService.findAllWithFilter(null, null, null, "INVALID", pageable)
        );
    }

    // insert colis information
    @Test
    void testSaveColis_NewReceiverAndSender_ShouldCreateThem() {
        ColisRequestDTO dto = new ColisRequestDTO();
        dto.setVileDistination("Agadir");
        dto.setPriority(Priority.NORMALE);

        Zone zone1 = new Zone();
        zone1.setId("city1");
        dto.setCity(zone1);

        Receiver receiver = new Receiver();
        receiver.setId(null);
        receiver.setNom("Ali");
        dto.setReceiver(receiver);

        Sender sender = new Sender();
        sender.setId(null);
        sender.setNom("Sara");
        dto.setSender(sender);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setNom("Phone");
        productDTO.setCategory("Electronics");
        productDTO.setPoids(1.0);
        productDTO.setPrice(1000.0);
        productDTO.setQuantity(2);
        dto.setProducts(List.of(productDTO));

        when(colisMapper.toEntity(dto)).thenReturn(new Colis());
        when(cityService.findCityById("city1")).thenReturn(new ZoneResponseDTO());
        when(zoneMapper.toEntity(any())).thenReturn(new Zone());
        when(receiverRepository.save(any(Receiver.class))).thenReturn(new Receiver());
        when(senderRepository.save(any(Sender.class))).thenReturn(new Sender());
        when(productRepository.save(any())).thenReturn(new Products());
        when(colisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(colisMapper.toDTO(any())).thenReturn(new ColisResponseDTO());

        ColisResponseDTO response = colisService.saveColis(dto);

        assertNotNull(response);
        verify(receiverRepository, times(1)).save(any(Receiver.class));
        verify(senderRepository, times(1)).save(any(Sender.class));
    }

    @Test
    void testSaveColis_ProductNotFound_ShouldThrowException() {
        ColisRequestDTO dto = new ColisRequestDTO();
        dto.setVileDistination("Agadir");
        dto.setPriority(Priority.NORMALE);

        Zone zone1 = new Zone();
        zone1.setId("city1");
        zone1.setNom("Agadir");
        dto.setCity(zone1);

        Receiver receiver = new Receiver();
        dto.setReceiver(receiver);

        Sender sender = new Sender();
        dto.setSender(sender);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setId("prod1");
        productDTO.setQuantity(1);
        dto.setProducts(List.of(productDTO));

        when(colisMapper.toEntity(dto)).thenReturn(new Colis());

        when(cityService.findCityById("city1")).thenReturn(new ZoneResponseDTO());
        when(zoneMapper.toEntity(any())).thenReturn(new Zone());
        when(receiverService.findEntityById("rec1")).thenReturn(new Receiver());
        when(senderService.findEntityById("sen1")).thenReturn(new Sender());

        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> colisService.saveColis(dto)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void testSaveColis_WithExistingReceiverAndSender_ShouldFindThem() {
        Zone zone1 = new Zone();
        zone1.setId("zo1");
        zone1.setNom("Agadir");

        ColisRequestDTO dto = new ColisRequestDTO();
        dto.setVileDistination("Agadir");
        dto.setPriority(Priority.NORMALE);
        dto.setCity(zone1);

        Receiver existingReceiver = new Receiver();
        existingReceiver.setId("receiver-id-123");
        dto.setReceiver(existingReceiver);

        Sender existingSender = new Sender();
        existingSender.setId("sender-id-456");
        dto.setSender(existingSender);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setNom("Test Product");
        productDTO.setCategory("Test");
        productDTO.setPoids(1.0);
        productDTO.setPrice(100.0);
        productDTO.setQuantity(1);
        dto.setProducts(List.of(productDTO));

        when(receiverService.findEntityById("receiver-id-123")).thenReturn(new Receiver());
        when(senderService.findEntityById("sender-id-456")).thenReturn(new Sender());

        when(cityService.findCityById(anyString())).thenReturn(new ZoneResponseDTO());
        when(zoneMapper.toEntity(any())).thenReturn(new Zone());
        when(colisMapper.toEntity(any())).thenReturn(new Colis());
        when(productRepository.save(any(Products.class))).thenReturn(new Products());
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));
        when(colisMapper.toDTO(any(Colis.class))).thenReturn(new ColisResponseDTO());

        ColisResponseDTO response = colisService.saveColis(dto);

        assertNotNull(response);

        verify(receiverService, times(1)).findEntityById("receiver-id-123");
        verify(senderService, times(1)).findEntityById("sender-id-456");

        verify(receiverRepository, never()).save(any(Receiver.class));
        verify(senderRepository, never()).save(any(Sender.class));
    }

    // find Colis By id method test
    @Test
    void testFindColisById_Success(){
        Colis colisEntity = new Colis();
        colisEntity.setId("co1");

        ColisResponseDTO colisDTO = new ColisResponseDTO();
        colisDTO.setId("co1");

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colisEntity));

        when(colisMapper.toDTO(colisEntity)).thenReturn(colisDTO);

        ColisResponseDTO result = colisService.findColisById("co1");

        assertNotNull(result);
        assertEquals("co1", result.getId());
        verify(colisRepository, times(1)).findById("co1");
        verify(colisMapper, times(1)).toDTO(colisEntity);
    }

    @Test
    void  testFindColisById_ResourceNotFoundException(){
        Colis colisEntity = new Colis();
        colisEntity.setId("co1");

        ColisResponseDTO colisDTO = new ColisResponseDTO();
        colisDTO.setId("co1");

        when(colisRepository.findById("co1")).thenReturn(Optional.empty());

        when(colisMapper.toDTO(colisEntity)).thenReturn(colisDTO);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> colisService.findColisById("co1")
        );

        assertTrue(exception.getMessage().contains("Aucun colis avec id"));
    }

    // update colis method test
    @Test
    void testUpdateColis_Success() {
        // 1️⃣ Given existing Colis in DB
        Colis existingColis = new Colis();
        existingColis.setId("123");
        existingColis.setStatus(Status.CREATED);

        Zone existingZone = new Zone();
        existingZone.setNom("Agadir");
        existingColis.setCity(existingZone);

        existingColis.setHistoriqueLivraisonList(new ArrayList<>());

        ColisUpdateDTO dto = new ColisUpdateDTO();
        dto.setReceiverId("r1");
        dto.setSenderId("s1");
        dto.setLivreurId("l1");
        dto.setCityId("z1");

        Receiver receiver = new Receiver();
        Sender sender = new Sender();
        Livreur livreur = new Livreur();
        ZoneResponseDTO zoneDto = new ZoneResponseDTO();
        zoneDto.setNom("Agadir");

        Zone mappedZone = new Zone();
        mappedZone.setNom("Agadir");

        when(colisRepository.findById("123")).thenReturn(Optional.of(existingColis));
        when(receiverService.findEntityById("r1")).thenReturn(receiver);
        when(senderService.findEntityById("s1")).thenReturn(sender);
        when(livreurService.findEntityById("l1")).thenReturn(livreur);
        when(cityService.findCityById("z1")).thenReturn(zoneDto);
        when(zoneMapper.toEntity(zoneDto)).thenReturn(mappedZone);
        when(colisRepository.save(any(Colis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ColisResponseDTO responseDTO = new ColisResponseDTO();
        responseDTO.setId("123");
        when(colisMapper.toDTO(any(Colis.class))).thenReturn(responseDTO);

        doNothing().when(colisMapper).updateColisFromDto(dto, existingColis);

        ColisResponseDTO result = colisService.updateColis(dto, "123");

        assertNotNull(result);
        assertEquals("123", result.getId());

        verify(colisRepository).findById("123");
        verify(receiverService).findEntityById("r1");
        verify(senderService).findEntityById("s1");
        verify(livreurService).findEntityById("l1");
        verify(cityService).findCityById("z1");
        verify(zoneMapper).toEntity(zoneDto);
        verify(colisMapper).updateColisFromDto(dto, existingColis);
        verify(colisRepository).save(existingColis);
        verify(colisMapper).toDTO(existingColis);

        assertEquals(1, existingColis.getHistoriqueLivraisonList().size());
        assertEquals("Colis modifier par le Gestionnaire logistique",
                existingColis.getHistoriqueLivraisonList().get(0).getComment());
    }

    @Test
    void testUpdateColis_DifferentZone_ThrowsException() {
        Colis existingColis = new Colis();
        existingColis.setId("123");
        Zone existingZone = new Zone();
        existingZone.setNom("Agadir");
        existingColis.setCity(existingZone);

        ColisUpdateDTO dto = new ColisUpdateDTO();
        dto.setCityId("z1");

        ZoneResponseDTO zoneDto = new ZoneResponseDTO();
        zoneDto.setNom("Casablanca");

        when(colisRepository.findById("123")).thenReturn(Optional.of(existingColis));
        when(cityService.findCityById("z1")).thenReturn(zoneDto);

        assertThrows(OperationNotAllowedException.class, () -> {
            colisService.updateColis(dto, "123");
        });

        verify(colisRepository, never()).save(any());
    }

    @Test
    void testUpdateColis_NotFound() {
        when(colisRepository.findById("999")).thenReturn(Optional.empty());

        ColisUpdateDTO dto = new ColisUpdateDTO();
        assertThrows(ResourceNotFoundException.class, () -> {
            colisService.updateColis(dto, "999");
        });

        verify(colisRepository, never()).save(any());
    }

    // delete colis method test
    @Test
    void testDeleteColis_Success(){
        Colis colis1 = new Colis();
        colis1.setId("co1");

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis1));

        colisService.deleteColis("co1");

        verify(colisRepository).delete(any(Colis.class));
        verify(colisRepository).findById("co1");
    }

    @Test
    void testDeleteColis_ResourceNotFoundException(){
        Colis colis1 = new Colis();
        colis1.setId("co1");

        when(colisRepository.findById("co2")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> colisService.deleteColis("co1")
        );
    }

    // get summary test
    @Test
    void getColisSummary_Success(){
        Zone zone1 = new Zone();
        zone1.setId("zo1");
        zone1.setNom("Agadir");

        Colis co1 = new Colis();
        co1.setId("co1");
        co1.setStatus(Status.CREATED);
        co1.setPriority(Priority.NORMALE);
        co1.setCity(zone1);

        Colis co2 = new Colis();
        co2.setId("co2");
        co2.setStatus(Status.IN_STOCK);
        co2.setPriority(Priority.NORMALE);
        co2.setCity(zone1);

        when(colisRepository.findAll()).thenReturn(List.of(co1, co2));

        Map<String, Object> summary = colisService.getColisSummary();

        assertNotNull(summary);
        assertEquals(3, summary.size());
    }

    // affect colis a livreur method test
    @Test
    void affectColisToLivreur_Success(){
        Zone zone1 = new Zone();
        zone1.setId("zo1");
        zone1.setNom("Agadir");

        Colis colis1 = new Colis();
        colis1.setId("co1");
        colis1.setStatus(Status.CREATED);
        colis1.setCity(zone1);
        colis1.setHistoriqueLivraisonList(new ArrayList<>());

        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setCity(zone1);

        ColisResponseDTO expectedResponse = new ColisResponseDTO();

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis1));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);
        when(colisMapper.toDTO(colis)).thenReturn(expectedResponse);

        ColisResponseDTO actualResponse = colisService.affectColisToLivreur("li1", "co1");

        verify(colisRepository, times(1)).save(colis1);
        verify(emailService, times(1)).sendColisAssignedEmail(colis1, livreur);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void affectColisToLivreur_ColisNotFoundException(){
        Zone zone1 = new Zone();
        zone1.setId("zo1");
        zone1.setNom("Agadir");

        Colis colis1 = new Colis();
        colis1.setId("co1");

        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setCity(zone1);

        when(colisRepository.findById("co1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                colisService.affectColisToLivreur("li1", "co1")
        );

        verify(colisRepository, never()).save(colis1);
    }

    @Test
    void affectColisToLivreur_ColisInStockAndLivreurNotInMaroc_ShouldThrowException() {
        Zone zoneLivreur = new Zone();
        zoneLivreur.setNom("Agadir");

        Zone zoneColis = new Zone();
        zoneColis.setNom("Agadir");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.IN_STOCK);
        colis.setCity(zoneColis);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setCity(zoneLivreur);

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> colisService.affectColisToLivreur("li1", "co1"));

        assertEquals("Colis est en stock, le livreur doit étre avec zone nom 'Maroc'!", exception.getMessage());
        verify(colisRepository, never()).save(any());
        verify(emailService, never()).sendColisAssignedEmail(any(), any());
    }

    @Test
    void affectColisToLivreur_LivreurAlreadyAssigned_ShouldThrowException() {
        Zone zone = new Zone();
        zone.setId("zo1");
        zone.setNom("Agadir");

        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setCity(zone);

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setCity(zone);
        colis.setStatus(Status.CREATED);
        colis.setLivreur(livreur);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> colisService.affectColisToLivreur("li1", "co1"));

        assertEquals("livreur est deja affecter sur ce Colis", exception.getMessage());
        verify(colisRepository, never()).save(any());
        verify(emailService, never()).sendColisAssignedEmail(any(), any());
    }

    @Test
    void affectColisToLivreur_DifferentCityAndNotInStock_ShouldThrowException() {
        Zone zoneColis = new Zone();
        zoneColis.setId("z1");
        zoneColis.setNom("Agadir");

        Zone zoneLivreur = new Zone();
        zoneLivreur.setId("z2");
        zoneLivreur.setNom("Casablanca");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.CREATED);
        colis.setCity(zoneColis);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setCity(zoneLivreur);

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> colisService.affectColisToLivreur("li1", "co1"));

        assertEquals("livreur ville est different de colis ville!", exception.getMessage());
        verify(colisRepository, never()).save(any());
        verify(emailService, never()).sendColisAssignedEmail(any(), any());
    }

    // update colis by livreur
    @Test
    void updateColisByLivreur_StatusChanged_ShouldAddHistoriqueAndSendEmail() {
        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setNom("Ali");
        livreur.setPrenom("Amine");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.CREATED);
        colis.setLivreur(livreur);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        ColisResponseDTO expected = new ColisResponseDTO();

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(expected);

        ColisResponseDTO result = colisService.updateColisByLivreur("li1", Status.LIVRED, "co1");

        assertEquals(Status.LIVRED, colis.getStatus());
        assertEquals(1, colis.getHistoriqueLivraisonList().size());
        verify(emailService, times(1))
                .sendColisStatusUpdatedEmail(colis, livreur, Status.CREATED, Status.LIVRED);
        verify(colisRepository, times(1)).save(colis);
        assertEquals(expected, result);
    }

    @Test
    void updateColisByLivreur_StatusNotChanged_ShouldNotSendEmailOrAddHistorique() {
        Livreur livreur = new Livreur();
        livreur.setId("li1");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.CREATED);
        colis.setLivreur(livreur);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        ColisResponseDTO expected = new ColisResponseDTO();

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(expected);

        ColisResponseDTO result = colisService.updateColisByLivreur("li1", Status.CREATED, "co1");

        assertEquals(Status.CREATED, colis.getStatus());
        assertTrue(colis.getHistoriqueLivraisonList().isEmpty());
        verify(emailService, never()).sendColisStatusUpdatedEmail(any(), any(), any(), any());
        assertEquals(expected, result);
    }

    @Test
    void updateColisByLivreur_ColisNotFound_ShouldThrowException() {
        when(colisRepository.findById("co1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                colisService.updateColisByLivreur("li1", Status.CREATED, "co1"));

        verify(colisRepository, never()).save(any());
        verify(emailService, never()).sendColisStatusUpdatedEmail(any(), any(), any(), any());
    }

    @Test
    void updateColisByLivreur_NotAssignedLivreur_ShouldThrowException() {
        Livreur assignedLivreur = new Livreur();
        assignedLivreur.setId("li2");

        Livreur currentLivreur = new Livreur();
        currentLivreur.setId("li1");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.CREATED);
        colis.setLivreur(assignedLivreur);

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(currentLivreur);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class, () ->
                colisService.updateColisByLivreur("li1", Status.LIVRED, "co1"));

        assertEquals("You can't change statut for colis not assigned to you!", exception.getMessage());
        verify(emailService, never()).sendColisStatusUpdatedEmail(any(), any(), any(), any());
    }

    @Test
    void updateColisByLivreur_StatusChangedToInStock_ShouldRemoveLivreur() {
        Livreur livreur = new Livreur();
        livreur.setId("li1");
        livreur.setNom("Ali");
        livreur.setPrenom("Amine");

        Colis colis = new Colis();
        colis.setId("co1");
        colis.setStatus(Status.CREATED);
        colis.setLivreur(livreur);
        colis.setHistoriqueLivraisonList(new ArrayList<>());

        ColisResponseDTO expected = new ColisResponseDTO();

        when(colisRepository.findById("co1")).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById("li1")).thenReturn(livreur);
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(expected);

        ColisResponseDTO result = colisService.updateColisByLivreur("li1", Status.IN_STOCK, "co1");

        assertNull(colis.getLivreur());
        assertEquals(Status.IN_STOCK, colis.getStatus());
        assertEquals(1, colis.getHistoriqueLivraisonList().size());
        verify(emailService, times(1))
                .sendColisStatusUpdatedEmail(colis, livreur, Status.CREATED, Status.IN_STOCK);
        assertEquals(expected, result);
    }

    @Test
    void findAllColisForClient_NoColis_ShouldThrowException() {
        String senderId = "client1";

        when(colisRepository.findColisBySender_Id(senderId)).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> colisService.findAllColisForClient(senderId));

        assertEquals("Aucun Colis pour ce client", exception.getMessage());
        verify(colisMapper, never()).toResponseDTOList(any());
    }

    @Test
    void findAllColisForClient_WithColis_ShouldReturnDTOList() {
        String senderId = "client1";

        Colis colis1 = new Colis();
        colis1.setId("co1");
        Colis colis2 = new Colis();
        colis2.setId("co2");

        List<Colis> colisList = List.of(colis1, colis2);

        List<ColisResponseDTO> dtoList = new ArrayList<>();
        dtoList.add(new ColisResponseDTO());
        dtoList.add(new ColisResponseDTO());

        when(colisRepository.findColisBySender_Id(senderId)).thenReturn(colisList);
        when(colisMapper.toResponseDTOList(colisList)).thenReturn(dtoList);

        List<ColisResponseDTO> result = colisService.findAllColisForClient(senderId);

        assertEquals(dtoList.size(), result.size());
        assertEquals(dtoList, result);
        verify(colisMapper, times(1)).toResponseDTOList(colisList);
    }

    @Test
    void findAllColisForReceiver_NoColis_ShouldThrowException() {
        String receiverId = "rec1";

        when(colisRepository.findColisByReceiver_Id(receiverId)).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> colisService.findAllColisForReciever(receiverId));

        assertEquals("No colis found for this receiver", exception.getMessage());
        verify(senderMapper, never()).toDTO(any());
    }

    @Test
    void findAllColisForReceiver_WithColis_ShouldReturnSummaryDTOList() {
        String receiverId = "rec1";

        Colis colis1 = new Colis();
        colis1.setStatus(Status.CREATED);
        colis1.setSender(new Sender());

        Colis colis2 = new Colis();
        colis2.setStatus(Status.LIVRED);
        colis2.setSender(new Sender());

        List<Colis> colisList = List.of(colis1, colis2);

        ColisSummaryDTO dto1 = new ColisSummaryDTO();
        ColisSummaryDTO dto2 = new ColisSummaryDTO();

        when(colisRepository.findColisByReceiver_Id(receiverId)).thenReturn(colisList);
        when(senderMapper.toDTO(colis1.getSender())).thenReturn(dto1.getSender());
        when(senderMapper.toDTO(colis2.getSender())).thenReturn(dto2.getSender());

        List<ColisSummaryDTO> result = colisService.findAllColisForReciever(receiverId);

        assertEquals(2, result.size());
        assertEquals(colisList.get(0).getStatus(), result.get(0).getStatus());
        assertEquals(colisList.get(1).getStatus(), result.get(1).getStatus());
        verify(senderMapper, times(1)).toDTO(colis1.getSender());
        verify(senderMapper, times(1)).toDTO(colis2.getSender());
    }
}
