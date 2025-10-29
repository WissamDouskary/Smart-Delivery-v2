package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.ReceiverRequestDTO;
import com.smartlogi.dto.responseDTO.ReceiverResponseDTO;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.ReceiverMapper;
import com.smartlogi.model.Receiver;
import com.smartlogi.repository.ReceiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiverService {
    private ReceiverRepository receiverRepository;
    private ReceiverMapper receiverMapper;

    @Autowired
    public ReceiverService(ReceiverRepository receiverRepository, ReceiverMapper receiverMapper){
        this.receiverRepository = receiverRepository;
        this.receiverMapper = receiverMapper;
    }

    public ReceiverResponseDTO saveReciever(ReceiverRequestDTO dto){
        Receiver receiver = receiverMapper.toEntity(dto);
        Receiver saved = receiverRepository.save(receiver);
        return receiverMapper.toResponseDTO(receiver);
    }

    public ReceiverResponseDTO findById(String id){
        Receiver receiver = receiverRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun receiver avec ce id: "+id));
        return receiverMapper.toResponseDTO(receiver);
    }
}
