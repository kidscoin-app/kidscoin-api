package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateChildRequest {

    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @NotBlank(message = "Username é obrigatório")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Username deve ter entre 3 e 20 caracteres (letras, números e _)")
    private String username;

    @NotNull(message = "Idade é obrigatória")
    @Positive(message = "Idade deve ser positiva")
    private Integer age;

    @NotBlank(message = "PIN é obrigatório")
    @Pattern(regexp = "\\d{4}", message = "PIN deve ter exatamente 4 dígitos")
    private String pin;

    private String avatarUrl;
}
