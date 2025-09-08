package co.com.pragma.autenticacion.api.exceptions;

import co.com.pragma.autenticacion.api.common.ApiResponse;
import co.com.pragma.autenticacion.api.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JsonAccessDeniedHandler implements ServerAccessDeniedHandler {
    private final ObjectMapper mapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        var status = HttpStatus.FORBIDDEN; // 403
        var req = exchange.getRequest();
        var method = (req.getMethod() != null) ? req.getMethod().name() : null;

        var err = ErrorResponse.builder()
                .errorCode("FORBIDDEN")
                .message("Acceso denegado")
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
