package co.com.pragma.autenticacion.api.exceptions;

import co.com.pragma.autenticacion.api.common.ApiResponse;
import co.com.pragma.autenticacion.api.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements ServerAuthenticationEntryPoint  {

    private final ObjectMapper mapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        var status = HttpStatus.UNAUTHORIZED; // 401
        var req = exchange.getRequest();
        var method = (req.getMethod() != null) ? req.getMethod().name() : null;

        var err = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED") // agrega en tu enum si quieres
                .message("No autenticado o token inválido")
                .httpStatus(status.value())
                .url(req.getURI().getPath())
                .method(method)
                .build();

        var body = ApiResponse.of(
                status.value(),
                "Solicitud inválida",
                err,
                req.getURI().getPath()
        );

        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            var bytes = mapper.writeValueAsBytes(body);
            return resp.writeWith(Mono.just(resp.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return resp.setComplete();
        }
    }
}
