package co.com.pragma.autenticacion.model.user.gateways;

public interface PasswordHasher {
    String encode(String raw);
    boolean matches(String raw, String encoded);
}
