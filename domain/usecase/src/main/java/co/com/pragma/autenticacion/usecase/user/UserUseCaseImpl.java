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
        String email = user.getEmail().trim().toLowerCase();

        return userRepository.existsByEmail(email)
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalArgumentException("Email already in use"))
                        : Mono.defer(() -> {
                            user.setEmail(email);
                            return userRepository.register(user);
                        })
                );
    }

    @Override
    public Flux<User> list() {
        return userRepository.findAllUsers();
    }

    @Override
    public Mono<User> getById(UUID id) {
        return userRepository.findUserById(id);
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}
