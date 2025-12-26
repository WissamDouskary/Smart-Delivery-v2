package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.CompleteProfileDTO;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.SenderMapper;
import com.smartlogi.delivery.model.Role;
import com.smartlogi.delivery.model.Sender;
import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.RoleRepository;
import com.smartlogi.delivery.repository.SenderRepository;
import com.smartlogi.delivery.repository.UserRepository; // Import correct repo
import com.smartlogi.security.helper.AuthenticatedUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SenderService {
    private final SenderRepository senderRepository;
    private final SenderMapper senderMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserHelper authenticatedUserHelper;

    @Autowired
    public SenderService(SenderRepository senderRepository,
                         SenderMapper senderMapper,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         AuthenticatedUserHelper authenticatedUserHelper){
        this.senderRepository = senderRepository;
        this.senderMapper = senderMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUserHelper = authenticatedUserHelper;
    }

    public SenderResponseDTO saveSender(SenderRequestDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }

        Role role = roleRepository.findByName("Sender")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'Sender' not found in database"));

        Sender s = senderMapper.toEntity(dto);

        User user = new User();
        user.setEmail(s.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(role);

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

    public SenderResponseDTO completeSenderProfile(CompleteProfileDTO dto) {
        User authUser = authenticatedUserHelper.getAuthenticatedUser();

        if (authUser.getSender() != null) {
            throw new IllegalStateException("Sender already exists");
        }

        Role senderRole = roleRepository.findByName("Sender")
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Sender sender = senderMapper.toProfileCompletionEntity(dto);
        sender.setUser(authUser);
        sender.setEmail(authUser.getEmail());
        authUser.setSender(sender);
        authUser.setRoleEntity(senderRole);

        senderRepository.save(sender);

        return senderMapper.toDTO(sender);
    }
}