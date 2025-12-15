package com.smartlogi.delivery.service;

import com.smartlogi.delivery.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.mapper.LivreurMapper;
import com.smartlogi.delivery.mapper.ZoneMapper;
import com.smartlogi.delivery.model.Livreur;
import com.smartlogi.delivery.model.Role;
import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.LivreurRepository;
import com.smartlogi.delivery.repository.RoleRepository; // Import
import com.smartlogi.delivery.repository.UserRepository;
import com.smartlogi.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LivreurService {
    private final LivreurRepository livreurRepository;
    private final LivreurMapper livreurMapper;
    private final CityService cityService;
    private final ZoneMapper zoneMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public LivreurService(UserRepository userRepository,
                          LivreurRepository livreurRepository,
                          LivreurMapper livreurMapper,
                          CityService cityService,
                          ZoneMapper zoneMapper,
                          RoleRepository roleRepository){
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
        this.cityService = cityService;
        this.zoneMapper = zoneMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public LivreurResponseDTO saveLivreur(LivreurRequestDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }

        Role role = roleRepository.findByName("Livreur")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'Livreur' not found"));

        Livreur livreur = livreurMapper.toEntity(dto);

        ZoneResponseDTO city = cityService.findCityById(dto.getCity().getId());
        livreur.setCity(zoneMapper.toEntity(city));
        livreur.setEmail(dto.getEmail());

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(SecurityConfig.passwordEncoder().encode(dto.getPassword()));

        user.setRoleEntity(role);

        livreur.setUser(user);
        user.setLivreur(livreur);

        Livreur saved = livreurRepository.save(livreur);

        return livreurMapper.toDTO(saved);
    }

    public LivreurResponseDTO findById(String id){
        Livreur livreur = livreurRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun sender avec id: "+id));
        return livreurMapper.toDTO(livreur);
    }

    public Livreur findEntityById(String id){
        return livreurRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livreur not found avec id: "+id));
    }
}