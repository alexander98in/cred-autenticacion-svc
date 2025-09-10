package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.UserConstraints;
import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.model.user.validator.UserValidator;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceAlreadyExistsException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import co.com.pragma.autenticacion.usecase.utils.ErrorCodeDomain;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public Mono<User> register(User user) {
        return Mono.zip(
                userRepository.existsByEmail(user.getEmail()),
                userRepository.existsByDocumentId(user.getDocumentId())
        ).flatMap(tuple -> {
            boolean emailExists = tuple.getT1();
            boolean documentExists = tuple.getT2();

            if (emailExists) {
                return Mono.error(new ResourceAlreadyExistsException(
                        ErrorCodeDomain.EMAIL_ALREADY_EXISTS.getCode(),
                        String.format(ErrorCodeDomain.EMAIL_ALREADY_EXISTS.getMessage(), user.getEmail())
                ));
            }

            if (documentExists) {
                return Mono.error(new ResourceAlreadyExistsException(
                        ErrorCodeDomain.DOCUMENT_NUMBER_ALREADY_EXISTS.getCode(),
                        String.format(ErrorCodeDomain.DOCUMENT_NUMBER_ALREADY_EXISTS.getMessage(), user.getDocumentId())
                ));
            }

            if (!UserValidator.isSalaryInRange(user.getSalary())) {
                String msg = String.format(ErrorCodeDomain.SALARY_RANGE.getMessage(),
                        UserConstraints.MIN_SALARY.toPlainString(), UserConstraints.MAX_SALARY.toPlainString());
                return Mono.error(new BusinessRuleViolationException(ErrorCodeDomain.SALARY_RANGE.getCode(), msg));
            }

            String hashed = passwordHasher.encode(user.getPassword());
            user.setPassword(hashed);
            return userRepository.register(user);
        });
    }

    @Override
    public Flux<User> listUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public Mono<User> getByDocumentId(String documentId) {
        return userRepository.findByDocumentId(documentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        ErrorCodeDomain.USER_NOT_FOUND.getCode(),
                        String.format(ErrorCodeDomain.USER_NOT_FOUND.getMessage(), "Document ID", documentId)
                )));
    }

    @Override
    public Mono<User> getById(UUID id) {
        return userRepository.findUserById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        ErrorCodeDomain.USER_NOT_FOUND.getCode(),
                        String.format(ErrorCodeDomain.USER_NOT_FOUND.getMessage(), "ID", id.toString())
                )));
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        ErrorCodeDomain.USER_NOT_FOUND.getCode(),
                        String.format(ErrorCodeDomain.USER_NOT_FOUND.getMessage(), "Email", email)
                )));
    }
}
