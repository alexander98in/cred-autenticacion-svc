package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.UserConstraints;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.model.user.validator.UserValidator;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceAlreadyExistsException;
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
        user.setEmail(email);

        if (!UserValidator.isSalaryInRange(user.getSalary())) {
            String msg = "El salario_base debe estar entre %s y %s"
                    .formatted(UserConstraints.MIN_SALARY.toPlainString(), UserConstraints.MAX_SALARY.toPlainString());
            return Mono.error(new BusinessRuleViolationException("USER_SALARY_OUT_OF_RANGE", msg));
        }

        return Mono.zip(
                userRepository.existsByEmail(email),
                userRepository.existsByDocumentId(user.getDocumentId())
            )
            .flatMap(tuple -> {
                boolean emailExists = tuple.getT1();
                boolean docExists   = tuple.getT2();

                if (emailExists) {
                    return Mono.error(new ResourceAlreadyExistsException("El email ya está registrado: " + email));
                }
                if (docExists) {
                    return Mono.error(new ResourceAlreadyExistsException("El documento_identidad ya está registrado: " + user.getDocumentId()));
                }
                return userRepository.register(user);
            });
    }

    @Override
    public Flux<User> listUsers() {
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

    @Override
    public Mono<User> getByDocumentId(String documentId) {
        return userRepository.findByDocumentId(documentId);
    }
}
