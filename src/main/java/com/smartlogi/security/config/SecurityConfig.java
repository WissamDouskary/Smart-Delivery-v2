package com.smartlogi.security.config;

import com.smartlogi.security.filter.JwtAuthenticationFilter;
import com.smartlogi.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authz -> authz

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers("/api/auth/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/colis/receiver/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/colis/{id}/historique").permitAll()

                        .requestMatchers(
                                "/api/statistiques/**",
                                "/api/search/**",
                                "/api/product/**"
                        ).hasRole("Manager")

                        .requestMatchers("/api/sender").hasRole("Manager")
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/sender").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/colis", "/api/colis/summary").hasRole("Manager")
                        .requestMatchers(HttpMethod.PUT, "/api/colis/{id}").hasRole("Manager")
                        .requestMatchers(HttpMethod.DELETE, "/api/colis/{id}").hasRole("Manager")
                        .requestMatchers(HttpMethod.PATCH, "/api/colis/affect/**").hasRole("Manager")
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/livreur").hasRole("Manager")

                        .requestMatchers(
                                "/api/colis/livreur/**",
                                "/api/colis/{colis_id}/livreur/{livreur_id}"
                        ).hasAnyRole("Livreur", "Manager")

                        .requestMatchers(HttpMethod.POST, "/api/colis").hasAnyRole("Sender", "Manager")
                        .requestMatchers(
                                "/api/colis/client/**",
                                "/api/sender/{id}",
                                "/api/receiver/**"
                        ).hasAnyRole("Sender", "Manager")

                        .requestMatchers(HttpMethod.GET, "/api/colis/{id}").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}