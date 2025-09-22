package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.common.ResponseUtil;
import co.com.pragma.autenticacion.api.dto.request.LoginRequestDTO;
import co.com.pragma.autenticacion.api.facade.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthFacade authFacade;
    private final SmartValidator validator;

    public Mono<ServerResponse> login(ServerRequest req) {

        return req.bodyToMono(LoginRequestDTO.class)
                .flatMap(dto -> {
                    var errors = new BeanPropertyBindingResult(dto, LoginRequestDTO.class.getName());
                    validator.validate(dto, errors);
                    if (errors.hasErrors()) {
                        var map = new java.util.LinkedHashMap<String, String>();
                        errors.getFieldErrors().forEach(fe -> map.put(fe.getField(), fe.getDefaultMessage()));
                        return Mono.error(new co.com.pragma.autenticacion.api.exceptions.RequestValidationException("Datos invalidos", map));
                    }
                    return Mono.just(dto);
                })
                .flatMap(authFacade::login)
                .flatMap(resp -> ResponseUtil.ok(req, "Login exitoso", resp));
    }
}
