package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateChildRequest;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.*;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.SavingsRepository;
import com.educacaofinanceira.repository.UserRepository;
import com.educacaofinanceira.repository.UserXPRepository;
import com.educacaofinanceira.repository.WalletRepository;
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
    private final WalletRepository walletRepository;
    private final UserXPRepository userXPRepository;
    private final SavingsRepository savingsRepository;

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

        // Valida se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        // Cria a criança
        User child = new User();
        child.setUsername(request.getUsername());
        child.setPassword(passwordEncoder.encode(request.getPin())); // PIN vira senha
        child.setFullName(request.getFullName());
        child.setRole(UserRole.CHILD);
        child.setFamily(parent.getFamily());
        child.setPin(request.getPin());
        child.setAvatarUrl(request.getAvatarUrl());
        child = userRepository.save(child);

        // Criar Wallet
        Wallet wallet = new Wallet();
        wallet.setChild(child);
        wallet.setBalance(0);
        wallet.setTotalEarned(0);
        wallet.setTotalSpent(0);
        walletRepository.save(wallet);

        // Criar UserXP
        UserXP userXP = new UserXP();
        userXP.setUser(child);
        userXP.setCurrentLevel(1);
        userXP.setCurrentXp(0);
        userXP.setTotalXp(0);
        userXPRepository.save(userXP);

        // Criar Savings
        Savings savings = new Savings();
        savings.setChild(child);
        savings.setBalance(0);
        savings.setTotalDeposited(0);
        savings.setTotalEarned(0);
        savingsRepository.save(savings);

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
        String emailOrUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Tenta buscar por email primeiro (PARENT), depois por username (CHILD)
        return userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
    }
}
