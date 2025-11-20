package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.enums.Role;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.SenderRepository;
import com.smartlogi.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SenderService {
    private final SenderRepository senderRepository;
    private final SenderMapper senderMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SenderRepository userRepository;

    @Autowired
    public SenderService(SenderRepository senderRepository, SenderMapper senderMapper){
        this.senderRepository = senderRepository;
        this.senderMapper = senderMapper;
    }

    public SenderResponseDTO saveSender(SenderRequestDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }

        Sender s = senderMapper.toEntity(dto);

        User user = new User();
        user.setEmail(s.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.Sender);

        s.setUser(user);
        user.setSender(s);

        Sender savedSender = senderRepository.save(s);
        return senderMapper.toDTO(savedSender);
    }

    public SenderResponseDTO findById(String id){
        Sender senderResponse = senderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun sender avec id: "+id));
        return senderMapper.toDTO(senderResponse);
    }

    public Sender findEntityById(String id) {
        return senderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
    }

    public List<SenderResponseDTO> findAll(){
        List<Sender> senders = senderRepository.findAll();
        if(senders.isEmpty()){
            throw new ResourceNotFoundException("aucun senders!");
        }
        return senderMapper.toResponseDTOList(senders);
    }
}
