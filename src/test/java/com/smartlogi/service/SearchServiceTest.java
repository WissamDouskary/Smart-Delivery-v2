package com.smartlogi.service;

import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.mapper.*;
import com.smartlogi.model.*;
import com.smartlogi.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock private ColisRepository colisRepository;
    @Mock private SenderRepository senderRepository;
    @Mock private ReceiverRepository receiverRepository;
    @Mock private LivreurRepository livreurRepository;

    @Mock private ColisMapper colisMapper;
    @Mock private SenderMapper senderMapper;
    @Mock private ReceiverMapper receiverMapper;
    @Mock private LivreurMapper livreurMapper;

    @InjectMocks
    private SearchService searchService;

    @Test
    void searchAll_WhenResultsFound_ShouldReturnMappedDTOs() {
        String keyword = "john";

        Colis colis = new Colis();
        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        Livreur livreur = new Livreur();

        ColisResponseDTO colisDTO = new ColisResponseDTO();
        SenderResponseDTO senderDTO = new SenderResponseDTO();
        ReceiverResponseDTO receiverDTO = new ReceiverResponseDTO();
        LivreurResponseDTO livreurDTO = new LivreurResponseDTO();

        when(colisRepository.findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword))
                .thenReturn(List.of(colis));
        when(senderRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of(sender));
        when(receiverRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(List.of(receiver));
        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword))
                .thenReturn(List.of(livreur));

        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);
        when(senderMapper.toDTO(sender)).thenReturn(senderDTO);
        when(receiverMapper.toResponseDTO(receiver)).thenReturn(receiverDTO);
        when(livreurMapper.toDTO(livreur)).thenReturn(livreurDTO);

        SearchResponseDTO result = searchService.searchAll(keyword);

        assertNotNull(result);
        assertEquals(1, result.getColis().size());
        assertEquals(1, result.getSenders().size());
        assertEquals(1, result.getReceivers().size());
        assertEquals(1, result.getLivreurs().size());

        verify(colisRepository).findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword);
        verify(senderRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(receiverRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword);
    }

    @Test
    void searchAll_WhenNoResults_ShouldReturnEmptyLists() {
        String keyword = "unknown";

        when(colisRepository.findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword))
                .thenReturn(Collections.emptyList());
        when(senderRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(Collections.emptyList());
        when(receiverRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword))
                .thenReturn(Collections.emptyList());
        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword))
                .thenReturn(Collections.emptyList());

        SearchResponseDTO result = searchService.searchAll(keyword);

        assertNotNull(result);
        assertTrue(result.getColis().isEmpty());
        assertTrue(result.getSenders().isEmpty());
        assertTrue(result.getReceivers().isEmpty());
        assertTrue(result.getLivreurs().isEmpty());

        verify(colisRepository).findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword);
        verify(senderRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(receiverRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword);
    }
}
