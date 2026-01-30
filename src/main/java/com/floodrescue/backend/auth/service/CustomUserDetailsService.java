package com.floodrescue.backend.auth.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    /**
     * Trả về authority có prefix ROLE_ để khớp với hasRole('ADMIN') / hasAnyRole(...).
     * Spring Security hasRole("ADMIN") tìm authority "ROLE_ADMIN"; nếu lưu "ADMIN" sẽ 403.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            String roleName = user.getRole().getName();
            String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
            return Collections.singletonList(new SimpleGrantedAuthority(authority));
        }
        return Collections.emptyList();
    }
}
