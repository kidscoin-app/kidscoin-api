package com.educacaofinanceira.util;

import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper para acessar informações do usuário autenticado
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;

    /**
     * Retorna o usuário autenticado atualmente
     */
    public User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
