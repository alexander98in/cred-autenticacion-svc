package co.com.pragma.autenticacion.model.auth;

import java.util.List;

public record TokenVerification(
        String subject,
        List<String> authorities,
        boolean valid
) {
}
