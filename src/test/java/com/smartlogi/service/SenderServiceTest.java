package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.model.Colis;
import com.smartlogi.model.Sender;
import com.smartlogi.repository.SenderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SenderServiceTest {
    @Mock private SenderRepository senderRepository;
    @Mock private SenderMapper senderMapper;

    @InjectMocks private SenderService senderService;

    @Test
    void testSaveSender_Success(){
        Sender sender = new Sender();
        sender.setId("sen1");

        SenderRequestDTO requestDTO = new SenderRequestDTO();
        SenderResponseDTO responseDTO = new SenderResponseDTO();

        when(senderMapper.toEntity(requestDTO)).thenReturn(sender);
        when(senderRepository.save(any(Sender.class))).thenReturn(sender);
//        when(senderMapper.toDTO(sender)).thenReturn(responseDTO);

        SenderResponseDTO saved = senderService.saveSender(requestDTO);

        verify(senderMapper, times(1)).toEntity(requestDTO);
        verify(senderRepository, times(1)).save(sender);
        verify(senderMapper, times(1)).toDTO(sender);
    }
}
