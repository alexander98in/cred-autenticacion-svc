package co.com.pragma.autenticacion.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest r = exchange.getRequest();
        String rid = r.getId();
        String path = r.getURI().getPath();
        String method = r.getMethod() != null ? r.getMethod().name() : "?";
        long start = System.currentTimeMillis();

        log.debug("[{}] => {} {}", rid, method, path);

        return chain.filter(exchange)
                .doFinally(s -> {
                    long ms = System.currentTimeMillis() - start;
                    Integer sc = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 0;
                    log.debug("[{}] <= {} {} [{}] {} ms", rid, method, path, sc, ms);
                });
    }
}
