package com.educacaofinanceira.security;

import com.educacaofinanceira.model.User;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Tenta buscar por email primeiro (PARENT), depois por username (CHILD)
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + emailOrUsername)));

        return buildUserDetails(user);
    }

    // Carrega usuário por ID
    public UserDetails loadUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userId));

        return buildUserDetails(user);
    }

    // Constrói UserDetails do Spring Security
    private UserDetails buildUserDetails(User user) {
        // Usa email para PARENT, username para CHILD
        String identifier = user.getEmail() != null ? user.getEmail() : user.getUsername();

        return org.springframework.security.core.userdetails.User.builder()
                .username(identifier)
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .build();
    }
}
