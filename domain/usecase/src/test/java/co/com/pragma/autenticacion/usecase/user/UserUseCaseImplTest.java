package co.com.pragma.autenticacion.usecase.user;


import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceAlreadyExistsException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import co.com.pragma.autenticacion.usecase.utils.ErrorCodeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserUseCaseImpl useCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new UserUseCaseImpl(userRepository, passwordHasher);

        validUser = User.builder()
                .id(UUID.randomUUID())
                .name("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .documentId("123456789")
                .phone("5551234")
                .salary(BigDecimal.valueOf(5000))
                .password("password123")
                .build();
    }

    // ==============================
    // CASOS DE REGISTER()
    // ==============================

    @Test
    void register_success() {
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId(validUser.getDocumentId())).thenReturn(Mono.just(false));
        when(passwordHasher.encode(validUser.getPassword())).thenReturn("hashedPassword");
        when(userRepository.register(validUser)).thenReturn(Mono.just(validUser.toBuilder().password("hashedPassword").build()));

        StepVerifier.create(useCase.register(validUser))
                .expectNextMatches(user -> user.getPassword().equals("hashedPassword") && user.getEmail().equals(validUser.getEmail()))
                .verifyComplete();
    }

    @Test
    void register_fail_emailAlreadyExists() {
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(Mono.just(true));
        when(userRepository.existsByDocumentId(validUser.getDocumentId())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.register(validUser))
                .expectErrorMatches(throwable -> throwable instanceof ResourceAlreadyExistsException &&
                        throwable.getMessage().contains("El email"))
                .verify();
    }

    @Test
    void register_fail_documentAlreadyExists() {
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId(validUser.getDocumentId())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(validUser))
                .expectErrorMatches(throwable -> throwable instanceof ResourceAlreadyExistsException &&
                        throwable.getMessage().contains("El numero de documento"))
                .verify();
    }

    @Test
    void register_fail_salaryOutOfRange() {
        User userWithBadSalary = validUser.toBuilder().salary(BigDecimal.valueOf(1)).build();

        when(userRepository.existsByEmail(userWithBadSalary.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId(userWithBadSalary.getDocumentId())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.register(userWithBadSalary))
                .expectErrorMatches(throwable -> throwable instanceof BusinessRuleViolationException &&
                        throwable.getMessage().contains("El salario"))
                .verify();
    }

    // ==============================
    // CASOS DE getByDocumentId()
    // ==============================

    @Test
    void getByDocumentId_success() {
        when(userRepository.findByDocumentId(validUser.getDocumentId())).thenReturn(Mono.just(validUser));

        StepVerifier.create(useCase.getByDocumentId(validUser.getDocumentId()))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void getByDocumentId_fail_notFound() {
        when(userRepository.findByDocumentId(validUser.getDocumentId())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getByDocumentId(validUser.getDocumentId()))
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Document ID"))
                .verify();
    }

    // ==============================
    // CASOS DE getById()
    // ==============================

    @Test
    void getById_success() {
        when(userRepository.findUserById(validUser.getId())).thenReturn(Mono.just(validUser));

        StepVerifier.create(useCase.getById(validUser.getId()))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void getById_fail_notFound() {
        when(userRepository.findUserById(validUser.getId())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getById(validUser.getId()))
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("ID"))
                .verify();
    }

    // ==============================
    // CASOS DE getByEmail()
    // ==============================

    @Test
    void getByEmail_success() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.just(validUser));

        StepVerifier.create(useCase.getByEmail(validUser.getEmail()))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void getByEmail_fail_notFound() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getByEmail(validUser.getEmail()))
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Email"))
                .verify();
    }

    // ==============================
    // CASOS DE listUsers()
    // ==============================

    @Test
    void listUsers_success() {
        when(userRepository.findAllUsers()).thenReturn(Flux.fromIterable(List.of(validUser)));

        StepVerifier.create(useCase.listUsers())
                .expectNext(validUser)
                .verifyComplete();
    }
}
