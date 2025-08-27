package co.com.pragma.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("nombres")
    private String name;

    @Column("apellidos")
    private String lastName;

    @Column("correo")
    private String email;

    @Column("documento_identidad")
    private String documentId;

    @Column("celular")
    private String phone;

    @Column("salario_base")
    private BigDecimal salary;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("direccion")
    private String address;

    @Column("id_rol")
    private UUID idRol;
}
