package co.com.pragma.autenticacion.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String name,
    String lastName,
    String email,
    String documentId,
    String phone,
    BigDecimal salary,
    LocalDate birthDate,
    String address,
    UUID idRol
) {
}
