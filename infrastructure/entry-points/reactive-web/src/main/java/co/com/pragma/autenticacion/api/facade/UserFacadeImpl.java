package co.com.pragma.autenticacion.api.facade;

import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.UserResponseDTO;
import co.com.pragma.autenticacion.api.mapper.UserDTOMapper;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserUseCase userUseCase;
    private final UserDTOMapper mapper;
    private final ReactiveTransactionManager txManager;

    private TransactionalOperator operator() { return TransactionalOperator.create(txManager); }

    @Override
    public Mono<UserResponseDTO> register(UserRequestDTO dto) {
        return Mono.just(dto)
                .map(mapper::toDomain)
                .flatMap(u -> userUseCase.register(u).as(operator()::transactional))
                .map(mapper::toResponse);
    }

    @Override
    public Mono<UserResponseDTO> getById(UUID id) {
        return userUseCase.getById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario", id.toString())))
                .map(mapper::toResponse);
    }

    @Override
    public Flux<UserResponseDTO> list() {
        return userUseCase.listUsers().map(mapper::toResponse);
    }

    @Override
    public Mono<UserResponseDTO> getByDocumentNumber(String documentNumber) {
        return userUseCase.getByDocumentId(documentNumber).map(mapper::toResponse);
    }
}
