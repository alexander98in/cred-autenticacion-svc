package co.com.pragma.autenticacion.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio User
 *
 * Esta clase representa al usuario dentro del dominio de autenticación.
 * Define los datos personales, de contacto y salariales que son requeridos
 * para el registro en el sistema (HU-01).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String documentId;
    private String phone;
    private BigDecimal salary;
    private LocalDate birthDate;
    private String address;
    private UUID idRol;
    private String password;

}
