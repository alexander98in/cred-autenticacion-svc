package co.com.pragma.autenticacion.usecase.exceptions;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resource, String id) {
        super("RESOURCE_NOT_FOUND: ", resource + " no encontrado: " + id);
    }
}
