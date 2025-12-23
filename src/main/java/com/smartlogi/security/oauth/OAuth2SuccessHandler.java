package com.smartlogi.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.security.DTO.responseDTO.LoginResponse;
import com.smartlogi.security.service.CustomUserDetailsService;
import com.smartlogi.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;
    private ObjectMapper objectMapper;

    public OAuth2SuccessHandler(JwtService jwtService, CustomUserDetailsService customUserDetailsService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");

        if (providerId == null) {
            throw new IllegalStateException("Google provider ID is missing");
        }

        UserDetails userDetails;

        try {
            userDetails = customUserDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException ex) {
            userDetails = customUserDetailsService.createOAuth2User(email, providerId);
        }

        String token = jwtService.generateToken(userDetails);

        LoginResponse apiResponse = new LoginResponse();
        apiResponse.setToken(token);
        apiResponse.setUserRole(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(r -> r.startsWith("ROLE_"))
                .findFirst()
                .map(roleName -> roleName.replace("ROLE_", ""))
                .orElse("UNKNOWN")
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse)
        );
    }
}
