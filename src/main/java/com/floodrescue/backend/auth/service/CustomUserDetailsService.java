package com.floodrescue.backend.auth.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .disabled(!user.getIsActive())
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()));
        }
        return Collections.emptyList();
    }
}
