package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.common.ResponseUtil;
import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.exceptions.RequestValidationException;
import co.com.pragma.autenticacion.api.mapper.UserDTOMapper;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;
    private final UserDTOMapper mapper;
    private final SmartValidator validator;
    private final ReactiveTransactionManager txManager;
    private TransactionalOperator operator() { return TransactionalOperator.create(txManager); }

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .flatMap(this::validate)
                .map(mapper::toDomain)
                .flatMap(user -> userUseCase.register(user).as(operator()::transactional))
                .map(mapper::toResponse)
                .flatMap(resp -> ResponseUtil.created(
                        serverRequest, URI.create("/api/v1/usuarios/" + resp.id()), "Usuario creado exitosamente", resp));
    }

    private Mono<UserRequestDTO> validate(UserRequestDTO dto) {
        var errors = new BeanPropertyBindingResult(dto, UserRequestDTO.class.getName());
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            var map = new java.util.LinkedHashMap<String, String>();
            errors.getFieldErrors().forEach(fe -> map.put(fe.getField(), fe.getDefaultMessage()));
            throw new RequestValidationException("Datos inválidos en la solicitud", map);
        }
        return Mono.just(dto);
    }
}


