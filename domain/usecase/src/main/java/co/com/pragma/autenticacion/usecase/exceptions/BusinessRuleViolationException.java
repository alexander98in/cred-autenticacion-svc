package co.com.pragma.autenticacion.usecase.exceptions;

public class BusinessRuleViolationException extends DomainException{
    public BusinessRuleViolationException(String code, String message) {
        super(code, message);
    }

    public BusinessRuleViolationException(String message) {
        super("VIOLACION_DE_REGLA_DE_NEGOCIO: " + message);
    }
}
