package co.com.pragma.autenticacion.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRequestDTO(
    @Schema(example = "Juan Carlos")
    @NotBlank(message = "{user.name.required}")
    @Size(min=6, max=100, message = "{user.name.size}")
    String name,

    @Schema(example = "Perez Lopez")
    @NotBlank(message = "{user.lastname.required}")
    @Size(min=6, max=100, message = "{user.lastname.size}")
    String lastName,

    @Schema(example = "juan.perez@correo.com")
    @NotBlank(message = "{user.email.required}")
    @Size(min=8, max=150, message = "{user.email.size}")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{user.email.invalid}")
    String email,

    @Schema(example = "1061811110")
    @NotBlank(message = "{user.document_id.required}")
    @Size(min=6, max=20, message = "{user.document_id.size}")
    String documentId,

    @Schema(example = "3234703198")
    @Size(min = 10, max = 10, message = "{user.phone.size}")
    @Pattern(regexp = "\\d{10}", message = "{user.phone.invalid}")
    String phone,

    @Schema(example = "1200000")
    @NotNull(message = "{user.salary.required}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{user.salary.min}")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "{user.salary.max}")
    BigDecimal salary,

    @Schema(example = "1995-07-19")
    LocalDate birthDate,

    @Schema(example = "Calle 25 N #2-40")
    String address,

    @Schema(example = "be066aae-0556-47c8-9e4b-e2f3b5c63f08")
    String idRol,

    @Schema(example = "P@ssw0rd!")
    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, max = 20, message = "{user.password.size}")
    String password
) { }

