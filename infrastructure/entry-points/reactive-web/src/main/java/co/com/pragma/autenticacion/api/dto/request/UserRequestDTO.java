package co.com.pragma.autenticacion.api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRequestDTO(
    @NotBlank(message = "{user.name.required}")
    @Size(min=6, max=100, message = "{user.name.size}")
    String name,

    @NotBlank(message = "{user.lastname.required}")
    @Size(min=6, max=100, message = "{user.lastname.size}")
    String lastname,

    @NotBlank(message = "{user.email.required}")
    @Size(min=8, max=150, message = "{user.email.size}")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{user.email.invalid}")
    String email,

    @NotBlank(message = "{user.document_id.required}")
    @Size(min=6, max=20, message = "{user.document_id.size}")
    String documentId,

    @Size(min = 10, max = 10, message = "{user.phone.size}")
    @Pattern(regexp = "\\d{10}", message = "{user.phone.invalid}")
    String phone,

    @NotNull(message = "{user.salary.required}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{user.salary.min}")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "{user.salary.max}")
    BigDecimal salary,

    LocalDate birthDate,

    String address,

    String idRol
) { }

