package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.UserResponseDTO;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import co.com.pragma.autenticacion.usecase.user.UserUseCaseImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCaseImpl userUseCase;
    private SmartValidator validator;
    private final ReactiveTransactionManager txManager;
    private TransactionalOperator operator() { return TransactionalOperator.create(txManager); }

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)

                .map(dto -> User.builder()
                        .name(dto.name())
                        .lastName(dto.lastname())
                        .email(dto.email())
                        .documentId(dto.documentId())
                        .phone(dto.phone())
                        .salary(dto.salary())
                        .birthDate(dto.birthDate())
                        .address(dto.address())
                        .idRol(parseUUID(dto.idRol()))
                        .build())
                .flatMap(user -> userUseCase.register(user))
                .map(saved -> new UserResponseDTO(
                        saved.getId(), saved.getName(), saved.getLastName(), saved.getEmail(),
                        saved.getDocumentId(), saved.getPhone(), saved.getSalary(),
                        saved.getBirthDate(), saved.getAddress(), saved.getIdRol()))
                .flatMap(resp -> ServerResponse
                        .created(URI.create("/api/v1/usuarios/" + resp.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp))
                .onErrorResume(IllegalArgumentException.class,
                        e -> ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ApiError("VALIDATION_ERROR", e.getMessage())))
                .onErrorResume(IllegalStateException.class,
                        e -> ServerResponse.status(409).contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ApiError("CONFLICT", e.getMessage())));


    }

    private Mono<UserRequestDTO> validate(UserRequestDTO dto) {
        var errors = new BeanPropertyBindingResult(dto, UserRequestDTO.class.getName());
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            return Mono.error(new RuntimeException("Errores de validación: " + errors.toString()));
        }
        return Mono.just(dto);
    }

    private UUID parseUUID(String maybe) {
        try { return (maybe == null || maybe.isBlank()) ? null : UUID.fromString(maybe); }
        catch (Exception e) { throw new IllegalArgumentException("idRol debe ser UUID válido"); }
    }

    private record ApiError(String code, String message) {}
}


