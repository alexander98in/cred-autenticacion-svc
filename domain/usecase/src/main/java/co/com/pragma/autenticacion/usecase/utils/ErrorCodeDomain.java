package co.com.pragma.autenticacion.usecase.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodeDomain {

    SALARY_RANGE("CRED-6001", "El salario debe estar ente %s y %s"),
    EMAIL_ALREADY_EXISTS("CRED-3001", "El email %s ya esta registrado"),
    DOCUMENT_NUMBER_ALREADY_EXISTS("CRED-3002", "El numero de documento %s ya esta registrado"),
    USER_NOT_FOUND("CRED-4001", "Usuario no encontrado por %s: %s"),
    INVALID_CREDENTIALS("CRED-7003", "Las credenciales proporcionadas no son validas"),
    USER_WITHOUT_ROLES("CRED-4002", "El usuario no tiene rol(es) asignados");

    private final String code;
    private final String message;
}
