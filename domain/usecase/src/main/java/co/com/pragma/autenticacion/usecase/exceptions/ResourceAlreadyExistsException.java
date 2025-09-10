package co.com.pragma.autenticacion.usecase.exceptions;

public class ResourceAlreadyExistsException extends DomainException {
    public ResourceAlreadyExistsException(String code, String message) {
        super(code, "RECURSO_EXISTENTE: " + message);
    }

    public ResourceAlreadyExistsException(String message) {
        super("RECURSO_EXISTENTE: " + message);
    }
}
