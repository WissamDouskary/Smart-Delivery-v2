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
import com.smartlogi.delivery.repository.UserRepository;
import com.smartlogi.security.helper.AuthenticatedUserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SenderServiceTest {

    @Mock
    private SenderRepository senderRepository;

    @Mock
    private SenderMapper senderMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUserHelper authenticatedUserHelper;

    @InjectMocks
    private SenderService senderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ saveSender tests ------------------

    @Test
    void saveSender_ShouldReturnSenderResponseDTO_WhenSavedSuccessfully() {
        SenderRequestDTO dto = new SenderRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");

        Sender senderEntity = new Sender();
        Sender savedSender = new Sender();
        SenderResponseDTO responseDTO = new SenderResponseDTO();
        Role role = new Role();
        role.setName("Sender");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(roleRepository.findByName("Sender")).thenReturn(Optional.of(role));
        when(senderMapper.toEntity(dto)).thenReturn(senderEntity);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");
        when(senderRepository.save(senderEntity)).thenReturn(savedSender);
        when(senderMapper.toDTO(savedSender)).thenReturn(responseDTO);

        SenderResponseDTO result = senderService.saveSender(dto);

        assertNotNull(result);
        verify(userRepository).existsByEmail(dto.getEmail());
        verify(senderMapper).toEntity(dto);
        verify(senderRepository).save(senderEntity);
        verify(senderMapper).toDTO(savedSender);
    }

    @Test
    void saveSender_ShouldThrowException_WhenEmailAlreadyExists() {
        SenderRequestDTO dto = new SenderRequestDTO();
        dto.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> senderService.saveSender(dto));
        assertEquals("Un compte avec cet email existe déjà.", ex.getMessage());
        verify(userRepository).existsByEmail(dto.getEmail());
        verifyNoMoreInteractions(senderRepository, senderMapper);
    }

    // ------------------ findById tests ------------------

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

    // ------------------ completeSenderProfile tests ------------------

    @Test
    void completeSenderProfile_ShouldSaveAndReturnSenderDTO_WhenNotExists() {
        CompleteProfileDTO dto = new CompleteProfileDTO();
        User authUser = new User();
        Sender sender = new Sender();
        SenderResponseDTO responseDTO = new SenderResponseDTO();
        Role senderRole = new Role();
        senderRole.setName("Sender");

        when(authenticatedUserHelper.getAuthenticatedUser()).thenReturn(authUser);
        when(roleRepository.findByName("Sender")).thenReturn(Optional.of(senderRole));
        when(senderMapper.toProfileCompletionEntity(dto)).thenReturn(sender);
        when(senderMapper.toDTO(sender)).thenReturn(responseDTO);

        SenderResponseDTO result = senderService.completeSenderProfile(dto);

        assertNotNull(result);
        assertEquals(sender, authUser.getSender());
        assertEquals(senderRole, authUser.getRoleEntity());
        verify(senderRepository).save(sender);
        verify(senderMapper).toDTO(sender);
    }

    @Test
    void completeSenderProfile_ShouldThrowException_WhenSenderAlreadyExists() {
        CompleteProfileDTO dto = new CompleteProfileDTO();
        User authUser = new User();
        authUser.setSender(new Sender());

        when(authenticatedUserHelper.getAuthenticatedUser()).thenReturn(authUser);

        assertThrows(IllegalStateException.class, () -> senderService.completeSenderProfile(dto));
        verifyNoInteractions(senderRepository, senderMapper, roleRepository);
    }

    // ------------------ findEntityById tests ------------------

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

    // ------------------ findAll tests ------------------

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
