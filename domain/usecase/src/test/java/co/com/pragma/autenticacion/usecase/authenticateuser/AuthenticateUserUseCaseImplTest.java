package co.com.pragma.autenticacion.usecase.authenticateuser;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class AuthenticateUserUseCaseImplTest {

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private RolRepository rolRepository;
    private AuthenticateUserUseCaseImpl authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        rolRepository = mock(RolRepository.class);

        authenticateUserUseCase = new AuthenticateUserUseCaseImpl(userRepository, passwordHasher, rolRepository);
    }

    @Test
    void authenticate_userNotFound_shouldReturnResourceNotFoundException() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email.trim().toLowerCase())).thenReturn(Mono.empty());

        StepVerifier.create(authenticateUserUseCase.authenticate(email, "anyPassword"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(userRepository).findByEmail(email.trim().toLowerCase());
        verifyNoInteractions(passwordHasher, rolRepository);
    }

    @Test
    void authenticate_invalidPassword_shouldReturnBusinessRuleViolationException() {
        String email = "user@example.com";
        String rawPassword = "wrongPassword";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail(email.trim().toLowerCase())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(rawPassword, "hashedPassword")).thenReturn(false);

        StepVerifier.create(authenticateUserUseCase.authenticate(email, rawPassword))
                .expectError(BusinessRuleViolationException.class)
                .verify();

        verify(userRepository).findByEmail(email.trim().toLowerCase());
        verify(passwordHasher).matches(rawPassword, "hashedPassword");
        verifyNoInteractions(rolRepository);
    }

    @Test
    void authenticate_userWithoutRoles_shouldReturnBusinessRuleViolationException() {
        String email = "user@example.com";
        String rawPassword = "password";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail(email.trim().toLowerCase())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(rawPassword, "hashedPassword")).thenReturn(true);
        when(rolRepository.findRolesByUserId(user.getId())).thenReturn(Flux.empty());

        StepVerifier.create(authenticateUserUseCase.authenticate(email, rawPassword))
                .expectError(BusinessRuleViolationException.class)
                .verify();

        verify(userRepository).findByEmail(email.trim().toLowerCase());
        verify(passwordHasher).matches(rawPassword, "hashedPassword");
        verify(rolRepository).findRolesByUserId(user.getId());
    }

    @Test
    void authenticate_successful_shouldReturnAuthenticatedUser() {
        String email = "user@example.com";
        String rawPassword = "password";
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email(email)
                .name("John")
                .password("hashedPassword")
                .build();

        Rol rol1 = Rol.builder().name("admin").build();
        Rol rol2 = Rol.builder().name("user role").build();

        when(userRepository.findByEmail(email.trim().toLowerCase())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(rawPassword, "hashedPassword")).thenReturn(true);
        when(rolRepository.findRolesByUserId(userId)).thenReturn(Flux.just(rol1, rol2));

        StepVerifier.create(authenticateUserUseCase.authenticate(email, rawPassword))
                .expectNextMatches(authenticatedUser -> {
                    List<String> authorities = authenticatedUser.authorities();
                    return authenticatedUser.id().equals(userId.toString())
                            && authenticatedUser.email().equals(email)
                            && authenticatedUser.name().equals("John")
                            && authorities.contains("ADMIN")
                            && authorities.contains("USER_ROLE");
                })
                .verifyComplete();

        verify(userRepository).findByEmail(email.trim().toLowerCase());
        verify(passwordHasher).matches(rawPassword, "hashedPassword");
        verify(rolRepository).findRolesByUserId(userId);
    }
}
