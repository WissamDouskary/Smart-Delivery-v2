package com.smartlogi.service;

import com.smartlogi.dto.responseDTO.ColisResponseDTO;
import com.smartlogi.enums.Priority;
import com.smartlogi.enums.Status;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Zone;
import com.smartlogi.repository.CityRepository;
import com.smartlogi.repository.ColisRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ColisServiceTest {
    @Mock
    private ColisRepository colisRepository;
    @Mock
    private CityRepository cityRepository;

    @Mock
    private ColisMapper colisMapper;

    @InjectMocks
    private ColisService colisService;

    @InjectMocks
    private CityService cityService;

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
}
