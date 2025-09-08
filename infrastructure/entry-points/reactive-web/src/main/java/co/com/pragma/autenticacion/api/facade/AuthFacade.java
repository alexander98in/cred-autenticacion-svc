package co.com.pragma.autenticacion.api.facade;

import co.com.pragma.autenticacion.api.dto.request.LoginRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.LoginResponseDTO;
import reactor.core.publisher.Mono;

public interface AuthFacade {

    Mono<LoginResponseDTO> login(LoginRequestDTO request);
}
