package co.com.pragma.autenticacion.model.auth;

import java.util.List;

public record AuthenticatedUser(
        String id,
        String email,
        String name,
        List<String> authorities
) {
}
