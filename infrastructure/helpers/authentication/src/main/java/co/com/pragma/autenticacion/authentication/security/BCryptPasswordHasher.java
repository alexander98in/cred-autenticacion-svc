package co.com.pragma.autenticacion.authentication.security;

import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String raw) {
        return encoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String encoded) {
        System.out.println("\n\n\nRAW: " + raw + "\nENCODED: " + encoded + "\n\n\n");
        return encoder.matches(raw, encoded);
    }
}
