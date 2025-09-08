package co.com.pragma.autenticacion.api.config;

import co.com.pragma.autenticacion.api.exceptions.JsonAccessDeniedHandler;
import co.com.pragma.autenticacion.api.exceptions.JsonAuthenticationEntryPoint;
import co.com.pragma.autenticacion.model.auth.TokenVerification;
import co.com.pragma.autenticacion.model.auth.gateways.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint, JsonAccessDeniedHandler jsonAccessDeniedHandler) {
        var authFilter = new AuthenticationWebFilter(jwtAuthenticationManager());
        authFilter.setServerAuthenticationConverter(this::convert);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(ex -> ex
                        // públicos
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // protegidos
                        // Protegidos (orden específico → de más específico a más general)
                        .pathMatchers(HttpMethod.POST, "/api/v1/usuarios").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/listar")
                        .hasAnyAuthority("ADMIN", "ASESOR")
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/documento/**")
                        .hasAnyAuthority("ADMIN", "ASESOR", "CLIENTE")
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/email/**")
                        .hasAnyAuthority("ADMIN", "ASESOR", "CLIENTE")
                        .anyExchange().authenticated()
                )
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint) // 401 con JSON
                        .accessDeniedHandler(jsonAccessDeniedHandler)           // 403 con JSON
                )
                .build();
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        var authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return Mono.empty();
        var token = authHeader.substring(7);

        return tokenService.verify(token)
                .filter(TokenVerification::valid)
                .map(tv -> {
                    var authorities = tv.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    return new UsernamePasswordAuthenticationToken(
                            tv.subject(),
                            token,
                            authorities
                    );
                });
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager() {
        return authentication -> Mono.just(authentication);
    }
}
