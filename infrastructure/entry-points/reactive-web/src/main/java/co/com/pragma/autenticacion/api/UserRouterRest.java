package co.com.pragma.autenticacion.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class UserRouterRest {
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route(POST("/api/v1/usuarios"), handler::registerUser);
    }
}
