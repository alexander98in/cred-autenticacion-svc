package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.UserConstraints;
import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceAlreadyExistsException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UserUseCaseImpl(userRepository, passwordHasher);
    }

    private User baseUser() {
        return User.builder()
                .id(null)
                .name("Juan")
                .lastName("Pérez")
                .email("  JUAN.PEREZ@EXAMPLE.COM ")
                .documentId("1061811110")
                .phone("3234703198")
                .salary(new BigDecimal("1200000"))
                .birthDate(LocalDate.of(1995, 7, 19))
                .address("Calle 25 N")
                .idRol(UUID.randomUUID())
                .build();
    }

    @Test
    void register_ok_cuandoNoExisteEmailNiDocumento() {
        // given
        var in = baseUser();
        var normalizedEmail = "juan.perez@example.com";
        var saved = in.toBuilder().id(UUID.randomUUID()).email(normalizedEmail).build();

        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId("1061811110")).thenReturn(Mono.just(false));
        when(userRepository.register(any(User.class))).thenReturn(Mono.just(saved));

        // when & then
        StepVerifier.create(useCase.register(in))
                .expectNextMatches(u -> u.getId() != null && u.getEmail().equals(normalizedEmail))
                .verifyComplete();

        // Se llaman ambos chequeos por el zip
        verify(userRepository).existsByEmail(normalizedEmail);
        verify(userRepository).existsByDocumentId("1061811110");

        // Se registra con email normalizado
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).register(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(normalizedEmail);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_falla_porEmailDuplicado() {
        var in = baseUser();
        var normalizedEmail = "juan.perez@example.com";

        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(Mono.just(true));
        when(userRepository.existsByDocumentId("1061811110")).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.register(in))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ResourceAlreadyExistsException.class);
                    assertThat(ex.getMessage()).contains("email");
                })
                .verify();

        verify(userRepository).existsByEmail(normalizedEmail);
        // con zip, también se invoca el otro:
        verify(userRepository).existsByDocumentId("1061811110");
        verify(userRepository, never()).register(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_falla_porDocumentoDuplicado() {
        var in = baseUser();
        var normalizedEmail = "juan.perez@example.com";

        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId("1061811110")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.register(in))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ResourceAlreadyExistsException.class);
                    assertThat(ex.getMessage()).contains("documento_identidad");
                })
                .verify();

        verify(userRepository).existsByEmail(normalizedEmail);
        verify(userRepository).existsByDocumentId("1061811110");
        verify(userRepository, never()).register(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_falla_porSalarioFueraDeRango() {
        var in = baseUser().toBuilder()
                .salary(UserConstraints.MAX_SALARY.add(BigDecimal.ONE)) // > 15'000.000
                .build();

        StepVerifier.create(useCase.register(in))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessRuleViolationException.class);
                    assertThat(ex.getMessage()).contains("salario_base");
                })
                .verify();

        // La validación ocurre en Mono.fromCallable() ANTES de tocar el repo
        verifyNoInteractions(userRepository);
    }

    @Test
    void listUsers_ok() {
        var u1 = baseUser().toBuilder().id(UUID.randomUUID()).email("a@a.com").build();
        var u2 = baseUser().toBuilder().id(UUID.randomUUID()).email("b@b.com").build();

        when(userRepository.findAllUsers()).thenReturn(Flux.just(u1, u2));

        StepVerifier.create(useCase.listUsers())
                .expectNext(u1, u2)
                .verifyComplete();

        verify(userRepository).findAllUsers();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByDocumentId_ok() {
        var u = baseUser().toBuilder().id(UUID.randomUUID()).email("x@x.com").build();
        when(userRepository.findByDocumentId("1061811110")).thenReturn(Mono.just(u));

        StepVerifier.create(useCase.getByDocumentId("1061811110"))
                .expectNext(u)
                .verifyComplete();

        verify(userRepository).findByDocumentId("1061811110");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByDocumentId_notFound_lanzaExcepcionDominio() {
        when(userRepository.findByDocumentId("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getByDocumentId("999"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(userRepository).findByDocumentId("999");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_ok() {
        UUID id = UUID.randomUUID();
        var u = baseUser().toBuilder().id(id).email("y@y.com").build();
        when(userRepository.findUserById(id)).thenReturn(Mono.just(u));

        StepVerifier.create(useCase.getById(id))
                .expectNext(u)
                .verifyComplete();

        verify(userRepository).findUserById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_notFound_lanzaExcepcionDominio() {
        UUID id = UUID.randomUUID();
        when(userRepository.findUserById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getById(id))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(userRepository).findUserById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByEmail_ok_normalizaMinusculasYTrim() {
        var u = baseUser().toBuilder().id(UUID.randomUUID()).email("h@h.com").build();
        when(userRepository.findByEmail("h@h.com")).thenReturn(Mono.just(u));

        StepVerifier.create(useCase.getByEmail("  H@H.COM "))
                .expectNext(u)
                .verifyComplete();

        verify(userRepository).findByEmail("h@h.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByEmail_notFound_lanzaExcepcionDominio() {
        when(userRepository.findByEmail("a@a.com")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getByEmail("a@a.com"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(userRepository).findByEmail("a@a.com");
        verifyNoMoreInteractions(userRepository);
    }
}
