package co.com.pragma.autenticacion.authentication.security;

import co.com.pragma.autenticacion.model.auth.AuthenticatedUser;
import co.com.pragma.autenticacion.model.auth.TokenVerification;
import co.com.pragma.autenticacion.model.auth.gateways.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenService implements TokenService {

    private final SecretKey key;
    private final long ttlMillis;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlMillis = ttlSeconds * 1000L;
    }

    @Override
    public Mono<String> generate(AuthenticatedUser user) {
        Instant now = Instant.now();
        String jws = Jwts.builder()
                .setSubject(user.email() == null ? "unknown" : user.email())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttlMillis)))
                .claim("authorities", user.authorities())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return Mono.just(jws);
    }

    @Override
    public Mono<TokenVerification> verify(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            String subject = jws.getBody().getSubject();
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) jws.getBody().get("authorities", List.class);
            return Mono.just(new TokenVerification(subject, authorities, true));
        } catch (JwtException e) {
            return Mono.just(new TokenVerification(null, List.of(), false));
        }
    }
}
