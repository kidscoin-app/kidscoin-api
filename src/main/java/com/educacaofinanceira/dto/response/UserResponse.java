package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String role;
    private UUID familyId;
    private String avatarUrl;

    // Construtor a partir de User entity
    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setFamilyId(user.getFamily().getId());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
}
