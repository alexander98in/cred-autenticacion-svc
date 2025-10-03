package co.com.pragma.autenticacion.usecase.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolName {
    ADMIN("ADMIN"),
    CLIENT("CLIENTE"),
    ASESOR("ASESOR");;

    private final String value;
}
