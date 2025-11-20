package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.repository.SenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SenderServiceTest {

    private SenderRepository senderRepository;
    private SenderMapper senderMapper;
    private SenderService senderService;

    @BeforeEach
    void setUp() {
        senderRepository = mock(SenderRepository.class);
        senderMapper = mock(SenderMapper.class);
        senderService = new SenderService(senderRepository, senderMapper);
    }

    @Test
    void saveSender_ShouldReturnSenderResponseDTO_WhenSavedSuccessfully() {
        SenderRequestDTO dto = new SenderRequestDTO();
        Sender senderEntity = new Sender();
        Sender savedSender = new Sender();
        SenderResponseDTO responseDTO = new SenderResponseDTO();

        when(senderMapper.toEntity(dto)).thenReturn(senderEntity);
        when(senderRepository.save(senderEntity)).thenReturn(savedSender);
        when(senderMapper.toDTO(savedSender)).thenReturn(responseDTO);

        SenderResponseDTO result = senderService.saveSender(dto);

        assertNotNull(result);
        verify(senderMapper).toEntity(dto);
        verify(senderRepository).save(senderEntity);
        verify(senderMapper).toDTO(savedSender);
    }

    @Test
    void findById_ShouldReturnSenderResponseDTO_WhenSenderExists() {
        String senderId = "123";
        Sender sender = new Sender();
        SenderResponseDTO responseDTO = new SenderResponseDTO();

        when(senderRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(senderMapper.toDTO(sender)).thenReturn(responseDTO);

        SenderResponseDTO result = senderService.findById(senderId);

        assertNotNull(result);
        verify(senderRepository).findById(senderId);
        verify(senderMapper).toDTO(sender);
    }

    @Test
    void findById_ShouldThrowException_WhenSenderNotFound() {
        String senderId = "999";
        when(senderRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> senderService.findById(senderId));
        verify(senderRepository).findById(senderId);
        verifyNoMoreInteractions(senderMapper);
    }

    @Test
    void findEntityById_ShouldReturnEntity_WhenFound() {
        String senderId = "abc";
        Sender sender = new Sender();
        when(senderRepository.findById(senderId)).thenReturn(Optional.of(sender));

        Sender result = senderService.findEntityById(senderId);

        assertEquals(sender, result);
    }

    @Test
    void findEntityById_ShouldThrowRuntimeException_WhenNotFound() {
        String senderId = "404";
        when(senderRepository.findById(senderId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> senderService.findEntityById(senderId));
        assertEquals("Sender not found", ex.getMessage());
    }

    @Test
    void findAll_ShouldReturnList_WhenSendersExist() {
        Sender sender1 = new Sender();
        Sender sender2 = new Sender();
        List<Sender> senderList = Arrays.asList(sender1, sender2);
        List<SenderResponseDTO> dtoList = Arrays.asList(new SenderResponseDTO(), new SenderResponseDTO());

        when(senderRepository.findAll()).thenReturn(senderList);
        when(senderMapper.toResponseDTOList(senderList)).thenReturn(dtoList);

        List<SenderResponseDTO> result = senderService.findAll();

        assertEquals(2, result.size());
        verify(senderRepository).findAll();
        verify(senderMapper).toResponseDTOList(senderList);
    }

    @Test
    void findAll_ShouldThrowException_WhenNoSendersFound() {
        when(senderRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> senderService.findAll());
        verify(senderRepository).findAll();
        verifyNoInteractions(senderMapper);
    }
}
