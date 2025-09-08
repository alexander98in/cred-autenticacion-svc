package co.com.pragma.autenticacion.usecase.authenticateuser;

import co.com.pragma.autenticacion.model.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

public interface AuthenticateUserUseCase {

    Mono<AuthenticatedUser> authenticate(String email, String rawPassword);
}
