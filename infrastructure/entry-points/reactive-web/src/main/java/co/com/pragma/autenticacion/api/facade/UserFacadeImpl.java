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

    private TransactionalOperator operator() {
        return TransactionalOperator.create(txManager);
    }

    private <T> Mono<T> transactional(Mono<T> mono) {
        return mono.as(operator()::transactional);
    }

    @Override
    public Mono<UserResponseDTO> register(UserRequestDTO dto) {
        return Mono.defer(() -> {
            var domain = mapper.toDomain(dto);
            return transactional(
                    userUseCase.register(domain)
                            .map(mapper::toResponse)
            );
        });
    }

    @Override
    public Flux<UserResponseDTO> list() {
        return Flux.defer(() ->
                userUseCase.listUsers()
                        .map(mapper::toResponse)
        );
    }

    @Override
    public Mono<UserResponseDTO> getByDocumentNumber(String documentNumber) {
        return Mono.defer(() ->
                userUseCase.getByDocumentId(documentNumber)
                        .map(mapper::toResponse)
        );
    }

    @Override
    public Mono<UserResponseDTO> getById(UUID id) {
        return Mono.defer(() ->
                userUseCase.getById(id)
                        .map(mapper::toResponse)
        );
    }

    @Override
    public Mono<UserResponseDTO> getByEmail(String email) {
        return Mono.defer(() ->
                userUseCase.getByEmail(email)
                        .map(mapper::toResponse)
        );
    }
}
