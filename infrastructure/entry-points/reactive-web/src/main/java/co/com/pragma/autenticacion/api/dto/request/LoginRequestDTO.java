package co.com.pragma.autenticacion.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "{auth.email.required}")
        String email,

        @NotBlank(message = "{auth.password.required}")
        @Size(min = 8, max = 64, message = "{auth.password.size}")
        String password
) {}