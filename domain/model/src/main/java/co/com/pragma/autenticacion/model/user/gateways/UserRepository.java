package co.com.pragma.autenticacion.model.user.gateways;

import co.com.pragma.autenticacion.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {

    Mono<User> findByDocumentId(String documentId);

    Mono<User> findByEmail(String email);

    Mono<User> findUserById(UUID id);

    Mono<User> register(User user);

    Mono<Void> deleteById(UUID id);

    Mono<Boolean> existsByDocumentId(String documentId);

    Mono<Boolean> existsByEmail(String email);

    Flux<User> findAllUsers();
}
