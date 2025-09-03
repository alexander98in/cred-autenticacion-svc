package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.UserConstraints;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.model.user.validator.UserValidator;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceAlreadyExistsException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    public Mono<User> register(User user) {
        return Mono.fromCallable(() -> {
                    String normalizedEmail = user.getEmail().trim().toLowerCase();
                    user.setEmail(normalizedEmail);
                    if (!UserValidator.isSalaryInRange(user.getSalary())) {
                        String msg = "El salario_base debe estar entre %s y %s"
                                .formatted(UserConstraints.MIN_SALARY.toPlainString(),
                                        UserConstraints.MAX_SALARY.toPlainString());
                        throw new BusinessRuleViolationException("USER_SALARY_OUT_OF_RANGE", msg);
                    }
                    return user;
                })
                .flatMap(u -> {
                    record CheckResult(User user, boolean emailExists, boolean docExists) {}
                    return Mono.zip(
                            userRepository.existsByEmail(u.getEmail()),
                            userRepository.existsByDocumentId(u.getDocumentId()),
                            (emailExists, docExists) -> new CheckResult(u, emailExists, docExists)
                    );
                })
                .flatMap(result -> {
                    // Validación de duplicados
                    if (result.emailExists()) {
                        return Mono.error(new ResourceAlreadyExistsException(
                                "El email ya está registrado: " + result.user().getEmail()));
                    }
                    if (result.docExists()) {
                        return Mono.error(new ResourceAlreadyExistsException(
                                "El documento_identidad ya está registrado: " + result.user().getDocumentId()));
                    }
                    // Registro del usuario
                    return userRepository.register(result.user());
                });
    }

    @Override
    public Flux<User> listUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public Mono<User> getByDocumentId(String documentId) {
        return userRepository.findByDocumentId(documentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario", documentId)));
    }

    @Override
    public Mono<User> getById(UUID id) {
        return userRepository.findUserById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario", id.toString())));
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario", email)));
    }
}
