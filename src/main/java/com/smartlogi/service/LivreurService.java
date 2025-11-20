package com.smartlogi.service;

import com.smartlogi.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.dto.responseDTO.ZoneResponseDTO;
import com.smartlogi.enums.Role;
import com.smartlogi.exception.ResourceNotFoundException;
import com.smartlogi.mapper.LivreurMapper;
import com.smartlogi.mapper.ZoneMapper;
import com.smartlogi.model.Livreur;
import com.smartlogi.model.User;
import com.smartlogi.repository.LivreurRepository;
import com.smartlogi.repository.UserRepository;
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
    private final SecurityConfig securityConfig;

    @Autowired
    public LivreurService(SecurityConfig securityConfig, UserRepository userRepository, LivreurRepository livreurRepository, LivreurMapper livreurMapper, CityService cityService, ZoneMapper zoneMapper){
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
        this.cityService = cityService;
        this.zoneMapper = zoneMapper;
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
    }

    @Transactional
    public LivreurResponseDTO saveLivreur(LivreurRequestDTO dto){
        Livreur livreur = livreurMapper.toEntity(dto);

        ZoneResponseDTO city = cityService.findCityById(dto.getCity().getId());
        livreur.setCity(zoneMapper.toEntity(city));
        livreur.setEmail(dto.getEmail());

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(securityConfig.passwordEncoder().encode(dto.getPassword()));
        user.setRole(Role.Livreur);

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
