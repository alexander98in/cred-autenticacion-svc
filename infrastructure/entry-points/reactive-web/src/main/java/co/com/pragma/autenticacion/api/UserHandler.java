package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.common.ResponseUtil;
import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.exceptions.RequestValidationException;
import co.com.pragma.autenticacion.api.facade.UserFacade;
import co.com.pragma.autenticacion.api.mapper.UserDTOMapper;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
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

    private final UserFacade userFacade;
    private final SmartValidator validator;

    public Mono<ServerResponse> registerUser(ServerRequest req) {
        final String rid = req.exchange().getRequest().getId(); // requestId de Netty
        log.info("[{}] POST /api/v1/usuarios - procesando registro", rid);

        return req.bodyToMono(UserRequestDTO.class)
                .doOnNext(body -> log.debug("[{}] payload recibido (masked): email={}, documentId={}",
                        rid, maskEmail(body.email()), safe(body.documentId())))
                .flatMap(this::validate)
                .flatMap(userFacade::register)
                .doOnSuccess(resp -> log.info("[{}] usuario creado id={}", rid, resp.id()))
                .doOnError(e -> log.debug("[{}] error en registerUser: {}", rid, e.toString()))
                .flatMap(resp -> ResponseUtil.created(
                        req, URI.create("/api/v1/usuarios/" + resp.id()),
                        "Usuario creado exitosamente", resp));
    }

    public Mono<ServerResponse> listUsers(ServerRequest req) {
        final String rid = req.exchange().getRequest().getId();
        log.info("[{}] GET /api/v1/usuarios/listar", rid);

        return userFacade.list()
                .collectList()
                .doOnSuccess(list -> log.info("[{}] listado devuelto tamaño={}", rid, list.size()))
                .doOnError(e -> log.debug("[{}] error en listUsers: {}", rid, e.toString()))
                .flatMap(list -> ResponseUtil.ok(req, "Listado de usuarios", list));
    }

    public Mono<ServerResponse> getUserByDocumentNumber(ServerRequest req) {
        final String rid = req.exchange().getRequest().getId();
        var documentNumber = req.pathVariable("documentNumber");
        log.info("[{}] GET /api/v1/usuarios/documento/{} - buscando usuario", rid, documentNumber);

        return userFacade.getByDocumentNumber(documentNumber)
                .doOnSuccess(u -> log.info("[{}] usuario encontrado id={}", rid, u.id()))
                .doOnError(e -> log.debug("[{}] error en getUserByDocumentNumber: {}", rid, e.toString()))
                .flatMap(user -> ResponseUtil.ok(req, "Usuario encontrado", user));
    }

    private Mono<UserRequestDTO> validate(UserRequestDTO dto) {
        var errors = new BeanPropertyBindingResult(dto, UserRequestDTO.class.getName());
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            var map = new java.util.LinkedHashMap<String, String>();
            errors.getFieldErrors().forEach(fe -> map.put(fe.getField(), fe.getDefaultMessage()));
            log.warn("Solicitud inválida en registerUser: {}", map);
            throw new RequestValidationException("Datos inválidos en la solicitud", map);
        }
        return Mono.just(dto);
    }

    // Helpers para no exponer PII en logs
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "N/A";
        var at = email.indexOf('@');
        return (at <= 2 ? "***" : email.substring(0, 2) + "***") + email.substring(at);
    }

    private static String safe(String v) { return (v == null || v.isBlank()) ? "N/A" : v; }
}
