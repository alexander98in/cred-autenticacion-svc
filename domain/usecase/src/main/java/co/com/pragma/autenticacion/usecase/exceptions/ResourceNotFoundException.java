package co.com.pragma.autenticacion.usecase.exceptions;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }
}
