package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @NotBlank(message = "Nome da família é obrigatório")
    private String familyName;
}
