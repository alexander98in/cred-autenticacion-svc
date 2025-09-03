package co.com.pragma.autenticacion.api.facade;

import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.UserResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserFacade {

    Mono<UserResponseDTO> register(UserRequestDTO dto);
    Flux<UserResponseDTO> list();
    Mono<UserResponseDTO> getByDocumentNumber(String documentNumber);
    Mono<UserResponseDTO> getById(UUID id);

}
