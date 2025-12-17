package com.smartlogi.security.helper;

import com.smartlogi.delivery.model.User;
import com.smartlogi.delivery.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserHelper {

    private final UserRepository userRepository;

    public AuthenticatedUserHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findUserByEmail(email);
    }

    public boolean isManager(){
        return "Manager".equals(getAuthenticatedUser().getRoleName());
    }

    public boolean isSender(){
        return "Sender".equals(getAuthenticatedUser().getRoleName());
    }

    public boolean isLivreur(){
        return "Livreur".equals(getAuthenticatedUser().getRoleName());
    }
}