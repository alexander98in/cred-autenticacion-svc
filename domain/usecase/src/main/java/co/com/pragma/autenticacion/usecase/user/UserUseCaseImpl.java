package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import lombok.RequiredArgsConstructor;

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
