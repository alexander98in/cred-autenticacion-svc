package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.common.ResponseUtil;
import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.exceptions.RequestValidationException;
import co.com.pragma.autenticacion.api.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private static final String MDC_KEY = "requestId";

    private final UserFacade userFacade;
    private final SmartValidator validator;
    
    public Mono<ServerResponse> registerUser(ServerRequest req) {
        return Mono.defer(() -> {
            String requestId = MDC.get(MDC_KEY); // Tomamos el requestId del filter
            log.info("[{}] POST /api/v1/usuarios - procesando registro", requestId);
            return req.bodyToMono(UserRequestDTO.class)
                    .doOnNext(body -> log.debug("[{}] payload recibido (masked): {}", requestId, maskPayload(body)))
                    .flatMap(this::validate)
                    .flatMap(userFacade::register)
                    .doOnSuccess(resp -> log.info("[{}] usuario creado id={}", requestId, resp.id()))
                    .doOnError(e -> log.error("[{}] error en registerUser ", requestId, e))
                    .flatMap(resp -> ResponseUtil.created(
                            req,
                            URI.create("/api/v1/usuarios/" + resp.id()),
                            "Usuario creado exitosamente",
                            resp
                    ));
        });
    }

    public Mono<ServerResponse> listUsers(ServerRequest req) {
        return Mono.defer(() -> {
            String requestId = MDC.get(MDC_KEY);
            log.info("[{}] GET /api/v1/usuarios/listar", requestId);

            return userFacade.list()
                    .collectList()
                    .doOnSuccess(list -> log.info("[{}] listado devuelto tamaño={}", requestId, list.size()))
                    .doOnError(e -> log.error("[{}] error en listUsers ", requestId, e))
                    .flatMap(list -> ResponseUtil.ok(req, "Listado de usuarios", list));
        });
    }

    public Mono<ServerResponse> getUserByDocumentNumber(ServerRequest req) {
        return Mono.defer(() -> {
            String requestId = MDC.get(MDC_KEY);
            var documentNumber = req.pathVariable("documentNumber");

            log.info("[{}] GET /api/v1/usuarios/documento/{} - buscando usuario", requestId, documentNumber);

            return userFacade.getByDocumentNumber(documentNumber)
                    .doOnSuccess(u -> log.info("[{}] usuario encontrado id={}", requestId, u.id()))
                    .doOnError(e -> log.error("[{}] error en getUserByDocumentNumber ", requestId, e))
                    .flatMap(user -> ResponseUtil.ok(req, "Usuario encontrado", user));
        });
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest req) {
        return Mono.defer(() -> {
            String requestId = MDC.get(MDC_KEY);
            var email = req.pathVariable("email");

            log.info("[{}] GET /api/v1/usuarios/email/{} - buscando usuario", requestId, maskEmail(email));

            return userFacade.getByEmail(email)
                    .doOnSuccess(u -> log.info("[{}] usuario encontrado id={}", requestId, u.id()))
                    .doOnError(e -> log.error("[{}] error en getUserByEmail ", requestId, e))
                    .flatMap(user -> ResponseUtil.ok(req, "Usuario encontrado", user));
        });
    }

    private Mono<UserRequestDTO> validate(UserRequestDTO dto) {
        var errors = new BeanPropertyBindingResult(dto, UserRequestDTO.class.getName());
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            var map = new java.util.LinkedHashMap<String, String>();
            errors.getFieldErrors().forEach(fe -> map.put(fe.getField(), fe.getDefaultMessage()));
            log.warn("Solicitud invalida en registerUser: {}", map);
            throw new RequestValidationException("Datos invalidos en la solicitud", map);
        }
        return Mono.just(dto);
    }

    // Helpers para no exponer PII en logs
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "N/A";
        int at = email.indexOf('@');
        return (at <= 2 ? "***" : email.substring(0, 2) + "***") + email.substring(at);
    }

    private static String safe(String v) {
        return (v == null || v.isBlank()) ? "N/A" : v;
    }

    private static String maskPayload(UserRequestDTO dto) {
        return "email=" + maskEmail(dto.email()) + ", documentId=" + safe(dto.documentId());
    }
}
