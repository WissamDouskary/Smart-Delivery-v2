package com.smartlogi.security.service;

import com.smartlogi.delivery.exception.ResourceNotFoundException;
import com.smartlogi.delivery.model.Permission;
import com.smartlogi.delivery.model.Role;
import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.RoleRepository;
import com.smartlogi.delivery.repository.UserRepository;
import com.smartlogi.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Value("${init.password}")
    private String initPassword;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoleEntity().getName()));

        if (user.getRoleEntity().getPermissions() != null) {
            for (Permission permission : user.getRoleEntity().getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        boolean isProviderUser = user.getProvider() != null && user.getProviderId() != null;
        boolean isAdmin = user.getRoleEntity().getName().equalsIgnoreCase("ADMIN");

        if (isProviderUser && !isAdmin) {
            String providerName = user.getProvider();
            throw new UsernameNotFoundException(
                    "This account must be authenticated using "+providerName
            );
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public UserDetails createOAuth2User(String email, String provider_id) {
        Role roleEntity = roleRepository.findByName("Pending")
                .orElseThrow(() -> new ResourceNotFoundException("Pending not found"));

        User user = new User();
        user.setEmail(email);
        user.setPassword(SecurityConfig.passwordEncoder().encode(initPassword));
        user.setProvider("GOOGLE");
        user.setProviderId(provider_id);
        user.setRoleEntity(roleEntity);

        userRepository.save(user);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_"+roleEntity.getName()))
        );
    }
}