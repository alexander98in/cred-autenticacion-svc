package co.com.pragma.autenticacion.model.auth.gateways;

import co.com.pragma.autenticacion.model.auth.AuthenticatedUser;
import co.com.pragma.autenticacion.model.auth.TokenVerification;
import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<String> generate(AuthenticatedUser user);

    Mono<TokenVerification> verify(String token);

}
