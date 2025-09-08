package co.com.pragma.autenticacion.api.facade;

import co.com.pragma.autenticacion.api.dto.request.LoginRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.LoginResponseDTO;
import co.com.pragma.autenticacion.model.auth.gateways.TokenService;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.usecase.authenticateuser.AuthenticateUserUseCase;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final TokenService tokenService;

    @Override
    public Mono<LoginResponseDTO> login(LoginRequestDTO request) {
        return authenticateUserUseCase.authenticate(request.email(), request.password())
                .flatMap(tokenService::generate)
                .map(token -> new LoginResponseDTO(token, "Bearer"));
    }
}
