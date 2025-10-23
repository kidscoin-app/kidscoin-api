package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateChildRequest;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Retorna o usuário autenticado
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return UserResponse.fromUser(user);
    }

    // Cria uma criança (apenas PARENT pode)
    @Transactional
    public UserResponse createChild(CreateChildRequest request) {
        User parent = getAuthenticatedUser();

        // Verifica se o usuário é PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem criar perfis de crianças");
        }

        // Gera email automático para criança
        String childEmail = generateChildEmail(parent, request.getFullName());

        // Valida se email já existe (improvável, mas por segurança)
        if (userRepository.existsByEmail(childEmail)) {
            throw new IllegalArgumentException("Já existe um perfil com este nome");
        }

        // Cria a criança
        User child = new User();
        child.setEmail(childEmail);
        child.setPassword(passwordEncoder.encode(request.getPin())); // PIN vira senha
        child.setFullName(request.getFullName());
        child.setRole(UserRole.CHILD);
        child.setFamily(parent.getFamily());
        child.setPin(request.getPin());
        child.setAvatarUrl(request.getAvatarUrl());
        child = userRepository.save(child);

        return UserResponse.fromUser(child);
    }

    // Lista todas as crianças da família (apenas PARENT pode)
    public List<UserResponse> getChildren() {
        User parent = getAuthenticatedUser();

        // Verifica se o usuário é PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem listar crianças");
        }

        // Busca crianças da mesma família
        List<User> children = userRepository.findByFamilyIdAndRole(parent.getFamily().getId(), UserRole.CHILD);

        return children.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // Obtém o usuário autenticado
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    // Gera email automático para criança
    private String generateChildEmail(User parent, String childName) {
        String cleanName = childName.toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z.]", "");
        String familyId = parent.getFamily().getId().toString().substring(0, 8);
        return cleanName + "." + familyId + "@child.local";
    }
}
