package com.smartlogi.security.controller;

import com.smartlogi.delivery.dto.requestsDTO.LivreurRequestDTO;
import com.smartlogi.delivery.dto.requestsDTO.SenderRequestDTO;
import com.smartlogi.delivery.dto.responseDTO.ErrorResponse;
import com.smartlogi.delivery.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.delivery.dto.responseDTO.SenderResponseDTO;
import com.smartlogi.delivery.service.LivreurService;
import com.smartlogi.security.DTO.LoginRequest;
import com.smartlogi.security.DTO.LoginResponse;
import com.smartlogi.security.service.JwtService;
import com.smartlogi.delivery.service.SenderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            SenderService senderService,
            LivreurService livreurService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.senderService = senderService;
        this.passwordEncoder = passwordEncoder;
        this.livreurService = livreurService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request) {

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
                .findFirst()
                .map(tk -> tk.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setUserRole(role);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register/sender")
    public ResponseEntity<SenderResponseDTO> registerSender(@Valid @RequestBody SenderRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être nul.");
        }
        SenderResponseDTO savedSender = senderService.saveSender(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSender);
    }

    @PostMapping("/register/livreur")
    public ResponseEntity<LivreurResponseDTO> registerLivreur(@Valid @RequestBody LivreurRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être nul.");
        }
        LivreurResponseDTO savedLivreur = livreurService.saveLivreur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLivreur);
    }
}
