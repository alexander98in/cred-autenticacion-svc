package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz que define los casos de uso relacionados con la entidad User.
 */
public interface UserUseCase {

    /** Registra un usuario */
    Mono<User> register(User user);

    /** Lista todos los usuarios */
    Flux<User> listUsers();

    /** Consulta por id */
    Mono<User> getById(UUID id);

    /** Consulta por email */
    Mono<User> getByEmail(String email);

    Mono<User> getByDocumentId(String documentId);
}
