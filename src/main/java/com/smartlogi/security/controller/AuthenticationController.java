package com.smartlogi.security.controller;

import com.smartlogi.delivery.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ErrorResponse;
import com.smartlogi.delivery.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.UserRepository;
import com.smartlogi.delivery.service.LivreurService;
import com.smartlogi.security.DTO.requestDTO.LoginRequest;
import com.smartlogi.security.DTO.responseDTO.LoginResponse;
import com.smartlogi.security.service.JwtService;
import com.smartlogi.delivery.service.SenderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SenderService senderService;
    private final LivreurService livreurService;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            SenderService senderService,
            LivreurService livreurService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.senderService = senderService;
        this.livreurService = livreurService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "User not found", "Unauthorized"));
        }

        if(user.getProvider() != null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            "This account must be authenticated using Provider : " + user.getProvider(),
                            "Unauthorized"
                    )
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Email ou mot de passe incorrect. Veuillez vérifier vos identifiants.",
                            "Unauthorized"
                    )
            );
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        final String jwtToken = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .map(roleName -> roleName.replace("ROLE_", ""))
                .orElse("UNKNOWN");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setUserRole(role);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<SenderResponseDTO> defaultRegister(@Valid @RequestBody SenderRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être nul.");
        }
        SenderResponseDTO savedSender = senderService.saveSender(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSender);
    }

    @PreAuthorize("hasAnyRole('ROLE_Manager')")
    @PostMapping("/register/sender")
    public ResponseEntity<SenderResponseDTO> registerSender(@Valid @RequestBody SenderRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être nul.");
        }
        SenderResponseDTO savedSender = senderService.saveSender(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSender);
    }

    @PreAuthorize("hasAnyRole('ROLE_Manager')")
    @PostMapping("/register/livreur")
    public ResponseEntity<LivreurResponseDTO> registerLivreur(@Valid @RequestBody LivreurRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être nul.");
        }
        LivreurResponseDTO savedLivreur = livreurService.saveLivreur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLivreur);
    }
}