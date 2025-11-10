package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ColisProductsRequestDTO;
import com.smartlogi.dto.requestsDTO.ColisRequestDTO;
import com.smartlogi.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
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

import java.util.List;
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

    private Colis colis;
    private ColisResponseDTO colisDTO;
    private Zone zone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        colis = new Colis();
        zone = new Zone();
        colis.setId(String.valueOf(1L));
        colis.setStatus(Status.CREATED);
        colis.setPriority(Priority.NORMALE);
        colis.setVileDistination("Casablanca");
        zone.setNom("Agadir");
        colis.setCity(zone);
        colisDTO = new ColisResponseDTO();
        colisDTO.setId(String.valueOf(1L));
    }

    // find all colis with filter tests
    @Test
    void testFindAllWithFilter_ReturnsFilteredList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Colis> page = new PageImpl<>(List.of(colis));
        when(colisRepository.findAll(pageable)).thenReturn(page);
        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);

        Page<ColisResponseDTO> result = colisService.findAllWithFilter(
                "CREATED", "Agadir", "Casablanca", "NORMALE", pageable
        );

        assertEquals(1, result.getContent().size());
        assertEquals(colisDTO.getId(), result.getContent().get(0).getId());
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

    // insert colis information
    @Test
    void testSaveColis_NewSenderReceiverAndProduct(){
        ColisRequestDTO dto = new ColisRequestDTO();
        dto.setVileDistination("Agadir");
        dto.setPriority(Priority.NORMALE);

        Zone zone1 = new Zone();
        zone1.setId("city1");
        zone1.setNom("Casablanca");
        dto.setCity(zone1);

        Receiver receiver = new Receiver();
        dto.setReceiver(receiver);

        Sender sender = new Sender();
        dto.setSender(sender);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setNom("Phone");
        productDTO.setCategory("Electronics");
        productDTO.setPoids(1.0);
        productDTO.setPrice(1000.0);
        productDTO.setQuantity(2);
        dto.setProducts(List.of(productDTO));

        when(receiverMapper.toEntity(any())).thenReturn(receiver);
        when(senderMapper.toEntity(any())).thenReturn(sender);

        Colis colisEntity = new Colis();
        when(colisMapper.toEntity(any())).thenReturn(colisEntity);

        when(cityService.findCityById("city1")).thenReturn(new ZoneResponseDTO());
        when(zoneMapper.toEntity(any())).thenReturn(new Zone());
        when(receiverRepository.save(any())).thenReturn(receiver);
        when(senderRepository.save(any())).thenReturn(sender);
        when(productRepository.save(any())).thenReturn(new Products());
        when(colisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(colisMapper.toDTO(any())).thenReturn(new ColisResponseDTO());

        ColisResponseDTO response = colisService.saveColis(dto);

        assertNotNull(response);
        verify(receiverRepository, times(1)).save(any());
        verify(senderRepository, times(1)).save(any());
        verify(productRepository, times(1)).save(any());
        verify(colisRepository, times(1)).save(any());
        verify(emailService, times(1)).sendColisCreatedEmail(any());
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
    void testSaveColis_ReceiverAndSenderFound(){
        ColisRequestDTO dto = new ColisRequestDTO();
        dto.setVileDistination("Agadir");
        dto.setPriority(Priority.NORMALE);

        Zone zone1 = new Zone();
        zone1.setId("city1");
        zone1.setNom("Agadir");
        dto.setCity(zone1);

        Receiver receiver = new Receiver();
        receiver.setId("rec1");
        dto.setReceiver(receiver);

        Sender sender = new Sender();
        sender.setId("sen1");
        dto.setSender(sender);

        ColisProductsRequestDTO productDTO = new ColisProductsRequestDTO();
        productDTO.setNom("Phone");
        productDTO.setCategory("Electronics");
        productDTO.setPoids(1.0);
        productDTO.setPrice(1000.0);
        productDTO.setQuantity(2);
        dto.setProducts(List.of(productDTO));

        when(colisMapper.toEntity(dto)).thenReturn(new Colis());

        when(receiverService.findEntityById("rec1")).thenReturn(receiver);
        when(senderService.findEntityById("sen1")).thenReturn(sender);

        when(cityService.findCityById("city1")).thenReturn(new ZoneResponseDTO());
        when(zoneMapper.toEntity(any())).thenReturn(new Zone());
        when(productRepository.save(any())).thenReturn(new Products());
        when(colisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(colisMapper.toDTO(any())).thenReturn(new ColisResponseDTO());

        ColisResponseDTO response = colisService.saveColis(dto);

        assertNotNull(response);
        verify(receiverService, times(1)).findEntityById("rec1");
        verify(senderService, times(1)).findEntityById("sen1");
    }
}
