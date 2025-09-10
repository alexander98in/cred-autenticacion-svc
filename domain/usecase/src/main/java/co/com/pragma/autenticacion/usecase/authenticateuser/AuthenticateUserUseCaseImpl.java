package co.com.pragma.autenticacion.usecase.authenticateuser;

import co.com.pragma.autenticacion.model.auth.AuthenticatedUser;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.model.user.gateways.PasswordHasher;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.BusinessRuleViolationException;
import co.com.pragma.autenticacion.usecase.exceptions.ResourceNotFoundException;
import co.com.pragma.autenticacion.usecase.utils.ErrorCodeDomain;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements  AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RolRepository rolRepository;

    @Override
    public Mono<AuthenticatedUser> authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        ErrorCodeDomain.USER_NOT_FOUND.getCode(),
                        String.format(ErrorCodeDomain.USER_NOT_FOUND.getMessage(), "Email", email)
                )))
                .flatMap(user -> {
                    if(!passwordHasher.matches(rawPassword, user.getPassword())) {
                        return Mono.error(new BusinessRuleViolationException(
                                ErrorCodeDomain.INVALID_CREDENTIALS.getCode(),
                                ErrorCodeDomain.INVALID_CREDENTIALS.getMessage()
                        ));
                    }

                    return rolRepository.findRolesByUserId(user.getId())
                            .map(rol -> normalizeRoleName(rol.getName()))
                            .switchIfEmpty(Flux.error(
                                    new BusinessRuleViolationException(
                                            ErrorCodeDomain.USER_WITHOUT_ROLES.getCode(),
                                            String.format(ErrorCodeDomain.USER_WITHOUT_ROLES.getMessage())
                                    )
                            ))
                            .collectList()
                            .map(authorities -> new AuthenticatedUser(
                                    user.getId().toString(),
                                    user.getEmail(),
                                    user.getName(),
                                    authorities
                            ));
                });
    }

    private static String normalizeRoleName(String name) {
        if (name == null) return "USER";
        return name.trim().toUpperCase().replace(' ', '_');
    }
}
