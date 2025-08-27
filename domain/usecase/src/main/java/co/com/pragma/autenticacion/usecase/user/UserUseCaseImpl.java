package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    public Mono<User> register(User user) {
        return null;
    }

    @Override
    public Flux<User> list() {
        return null;
    }

    @Override
    public Mono<User> getById(UUID id) {
        return null;
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return null;
    }
}
