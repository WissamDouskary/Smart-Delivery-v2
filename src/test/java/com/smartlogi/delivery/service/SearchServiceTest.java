package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.responseDTO.*;
import com.smartlogi.delivery.mapper.ColisMapper;
import com.smartlogi.delivery.mapper.LivreurMapper;
import com.smartlogi.delivery.mapper.ReceiverMapper;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.model.Colis;
import com.smartlogi.delivery.model.Livreur;
import com.smartlogi.delivery.model.Receiver;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.repository.ColisRepository;
import com.smartlogi.delivery.repository.LivreurRepository;
import com.smartlogi.delivery.repository.ReceiverRepository;
import com.smartlogi.delivery.repository.SenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private ColisRepository colisRepository;
    @Mock
    private SenderRepository senderRepository;
    @Mock
    private ReceiverRepository receiverRepository;
    @Mock
    private LivreurRepository livreurRepository;
    @Mock
    private ColisMapper colisMapper;
    @Mock
    private SenderMapper senderMapper;
    @Mock
    private ReceiverMapper receiverMapper;
    @Mock
    private LivreurMapper livreurMapper;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchAll_ShouldReturnSearchResponseDTO_WithAllResults() {
        String keyword = "test";

        // Mock Colis
        Colis colis1 = new Colis();
        Colis colis2 = new Colis();
        ColisResponseDTO colisDTO1 = new ColisResponseDTO();
        ColisResponseDTO colisDTO2 = new ColisResponseDTO();
        when(colisRepository.findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword))
                .thenReturn(Arrays.asList(colis1, colis2));
        when(colisMapper.toDTO(colis1)).thenReturn(colisDTO1);
        when(colisMapper.toDTO(colis2)).thenReturn(colisDTO2);

        // Mock Sender
        Sender sender1 = new Sender();
        SenderResponseDTO senderDTO1 = new SenderResponseDTO();
        when(senderRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of(sender1));
        when(senderMapper.toDTO(sender1)).thenReturn(senderDTO1);

        // Mock Receiver
        Receiver receiver1 = new Receiver();
        ReceiverResponseDTO receiverDTO1 = new ReceiverResponseDTO();
        when(receiverRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of(receiver1));
        when(receiverMapper.toResponseDTO(receiver1)).thenReturn(receiverDTO1);

        // Mock Livreur
        Livreur livreur1 = new Livreur();
        LivreurResponseDTO livreurDTO1 = new LivreurResponseDTO();
        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword))
                .thenReturn(List.of(livreur1));
        when(livreurMapper.toDTO(livreur1)).thenReturn(livreurDTO1);

        // Call the service
        SearchResponseDTO result = searchService.searchAll(keyword);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.getColis().size());
        assertEquals(1, result.getSenders().size());
        assertEquals(1, result.getReceivers().size());
        assertEquals(1, result.getLivreurs().size());

        verify(colisRepository).findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword);
        verify(colisMapper).toDTO(colis1);
        verify(colisMapper).toDTO(colis2);

        verify(senderRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(senderMapper).toDTO(sender1);

        verify(receiverRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(receiverMapper).toResponseDTO(receiver1);

        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword);
        verify(livreurMapper).toDTO(livreur1);
    }

    @Test
    void searchAll_ShouldReturnEmptyLists_WhenNoResultsFound() {
        String keyword = "empty";

        when(colisRepository.findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword))
                .thenReturn(List.of());
        when(senderRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of());
        when(receiverRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of());
        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword))
                .thenReturn(List.of());

        SearchResponseDTO result = searchService.searchAll(keyword);

        assertNotNull(result);
        assertTrue(result.getColis().isEmpty());
        assertTrue(result.getSenders().isEmpty());
        assertTrue(result.getReceivers().isEmpty());
        assertTrue(result.getLivreurs().isEmpty());
    }
}
