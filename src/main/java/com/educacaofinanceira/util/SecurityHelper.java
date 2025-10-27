package com.educacaofinanceira.util;

import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper para acessar informações do usuário autenticado
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;

    /**
     * Retorna o usuário autenticado atualmente
     * Busca por email (PARENT) ou username (CHILD)
     * Usa JOIN FETCH para carregar Family EAGER, evitando LazyInitializationException
     */
    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        String emailOrUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Tenta buscar por email primeiro (PARENT), depois por username (CHILD)
        // Usa métodos com JOIN FETCH para carregar Family junto com User
        return userRepository.findByEmailWithFamily(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsernameWithFamily(emailOrUsername)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
    }
}
