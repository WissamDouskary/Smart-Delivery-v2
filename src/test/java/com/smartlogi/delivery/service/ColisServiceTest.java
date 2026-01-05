package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.delivery.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.delivery.exception.OperationNotAllowedException;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.ColisMapper;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.mapper.ZoneMapper;
import com.smartlogi.delivery.model.*;
import com.smartlogi.delivery.repository.*;
import com.smartlogi.delivery.enums.Status;
import com.smartlogi.delivery.mail.service.EmailService;
import com.smartlogi.security.helper.AuthenticatedUserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColisServiceTest {

    @Mock
    private ColisRepository colisRepository;
    @Mock
    private CityService cityService;
    @Mock
    private ColisMapper colisMapper;
    @Mock
    private ReceiverService receiverService;
    @Mock
    private SenderService senderService;
    @Mock
    private ZoneMapper zoneMapper;
    @Mock
    private SenderMapper senderMapper;
    @Mock
    private LivreurService livreurService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ReceiverRepository receiverRepository;
    @Mock
    private SenderRepository senderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticatedUserHelper authenticatedUserHelper;

    @InjectMocks
    private ColisService colisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(colisService, "initPassword", "default123");
    }

    @Test
    void saveColis_ShouldSaveAndReturnDTO_WhenSenderAndReceiverExist() {
        ColisRequestDTO dto = new ColisRequestDTO();
        Zone zone = new Zone();
        zone.setId("zone123");
        dto.setCity(zone);

        Receiver receiverDTO = new Receiver();
        receiverDTO.setEmail("receiver@test.com");
        receiverDTO.setNom("John");
        receiverDTO.setPrenom("Doe");
        dto.setReceiver(receiverDTO);

        Sender senderDTO = new Sender();
        senderDTO.setEmail("sender@test.com");
        senderDTO.setNom("Alice");
        senderDTO.setPrenom("Smith");
        dto.setSender(senderDTO);

        dto.setVileDistination("VilleTest");
        ZoneResponseDTO zoneResponseDTO = new ZoneResponseDTO();
        zoneResponseDTO.setNom("CityName");
        when(cityService.findCityById(any())).thenReturn(zoneResponseDTO);

        User authUser = new User();
        Sender authSender = new Sender();
        authUser.setSender(authSender);
        when(authenticatedUserHelper.getAuthenticatedUser()).thenReturn(authUser);
        when(authenticatedUserHelper.isSender()).thenReturn(true);

        Colis colisEntity = new Colis();
        when(colisMapper.toEntity(dto)).thenReturn(colisEntity);

        Colis savedColis = new Colis();
        when(colisRepository.save(colisEntity)).thenReturn(savedColis);

        ColisResponseDTO responseDTO = new ColisResponseDTO();
        when(colisMapper.toDTO(savedColis)).thenReturn(responseDTO);

        Role receiverRole = new Role();
        receiverRole.setName("Receiver");
        when(roleRepository.findByName("Receiver")).thenReturn(Optional.of(receiverRole));

        ColisResponseDTO result = colisService.saveColis(dto);

        assertNotNull(result);
        verify(colisRepository).save(colisEntity);
        verify(colisMapper).toDTO(savedColis);
        verify(emailService).sendColisCreatedEmail(savedColis);
        verify(receiverRepository).save(any(Receiver.class));
    }

    @Test
    void findColisById_ShouldReturnDTO_WhenColisExists() {
        String colisId = "123";
        Colis colis = new Colis();
        ColisResponseDTO colisDTO = new ColisResponseDTO();

        when(authenticatedUserHelper.isSender()).thenReturn(false);
        when(authenticatedUserHelper.isLivreur()).thenReturn(false);
        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);

        ColisResponseDTO result = colisService.findColisById(colisId);

        assertNotNull(result);
        verify(colisRepository).findById(colisId);
        verify(colisMapper).toDTO(colis);
    }

    @Test
    void findColisById_ShouldThrowException_WhenNotFound() {
        String colisId = "404";

        when(authenticatedUserHelper.isSender()).thenReturn(false);
        when(authenticatedUserHelper.isLivreur()).thenReturn(false);
        when(colisRepository.findById(colisId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> colisService.findColisById(colisId));
    }

    @Test
    void deleteColis_ShouldCallRepositoryDelete_WhenColisExists() {
        String colisId = "del123";
        Colis colis = new Colis();

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));

        colisService.deleteColis(colisId);

        verify(colisRepository).delete(colis);
    }

    @Test
    void deleteColis_ShouldThrowException_WhenColisNotFound() {
        String colisId = "del404";

        when(colisRepository.findById(colisId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> colisService.deleteColis(colisId));
    }

    @Test
    void affectColisToLivreur_ShouldAssignLivreurAndSendEmail_WhenValid() {
        String colisId = "c123";
        String livreurId = "l123";

        Colis colis = new Colis();
        colis.setStatus(Status.LIVRED);
        Zone zone = new Zone();
        zone.setId("zone123");
        colis.setCity(zone);
        Livreur livreur = new Livreur();
        livreur.setId(livreurId);
        livreur.setCity(zone);
        ColisResponseDTO dto = new ColisResponseDTO();

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(livreurService.findEntityById(livreurId)).thenReturn(livreur);
        when(colisRepository.save(colis)).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(dto);

        ColisResponseDTO result = colisService.affectColisToLivreur(livreurId, colisId);

        assertNotNull(result);
        verify(emailService).sendColisAssignedEmail(colis, livreur);
        verify(colisRepository).save(colis);
    }
}
