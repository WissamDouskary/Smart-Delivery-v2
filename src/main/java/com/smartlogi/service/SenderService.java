package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.model.Sender;
import com.smartlogi.repository.SenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SenderService {
    private SenderRepository senderRepository;
    private SenderMapper senderMapper;

    @Autowired
    public SenderService(SenderRepository senderRepository, SenderMapper senderMapper){
        this.senderRepository = senderRepository;
        this.senderMapper = senderMapper;
    }

    public SenderResponseDTO saveSender(SenderRequestDTO dto){
        Sender s = senderMapper.toEntity(dto);
        Sender savedSender = senderRepository.save(s);
        return senderMapper.toResponseDTO(savedSender);
    }

    public SenderResponseDTO findById(String id){
        Sender senderResponse = senderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun sender avec id: "+id));
        return senderMapper.toResponseDTO(senderResponse);
    }

    public Sender findEntityById(String id) {
        return senderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
    }
}
