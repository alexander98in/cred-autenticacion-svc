package co.com.pragma.autenticacion.api.dto.response;

import java.time.Instant;

public record LoginResponseDTO(
        String token,
        String tokenType
) {
}
