package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email ou username é obrigatório")
    private String emailOrUsername;

    @NotBlank(message = "Senha/PIN é obrigatório")
    private String password;
}
