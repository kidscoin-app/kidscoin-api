package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.LoginRequest;
import com.educacaofinanceira.dto.request.RefreshTokenRequest;
import com.educacaofinanceira.dto.request.RegisterRequest;
import com.educacaofinanceira.dto.response.AuthResponse;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Family;
import com.educacaofinanceira.model.RefreshToken;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.FamilyRepository;
import com.educacaofinanceira.repository.RefreshTokenRepository;
import com.educacaofinanceira.repository.UserRepository;
import com.educacaofinanceira.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Valida se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Cria a família
        Family family = new Family();
        family.setName(request.getFamilyName());
        family = familyRepository.save(family);

        // Cria o usuário PARENT
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(UserRole.PARENT);
        user.setFamily(family);
        user = userRepository.save(user);

        // Gera tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, UserResponse.fromUser(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Busca usuário por email ou username
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> userRepository.findByUsername(request.getEmailOrUsername())
                        .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas")));

        // Valida senha/PIN
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        // Deleta refresh tokens antigos do usuário
        refreshTokenRepository.deleteByUserId(user.getId());

        // Gera novos tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, UserResponse.fromUser(user));
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        // Busca o refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        // Valida se não está revogado
        if (refreshToken.getRevoked()) {
            throw new UnauthorizedException("Refresh token revogado");
        }

        // Valida se não expirou
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expirado");
        }

        // Busca o usuário
        User user = refreshToken.getUser();

        // Gera novo access token (mantém o mesmo refresh token)
        String accessToken = jwtTokenProvider.generateToken(user);

        return new AuthResponse(accessToken, request.getRefreshToken(), UserResponse.fromUser(user));
    }

    // Cria um novo refresh token
    private String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }
}
