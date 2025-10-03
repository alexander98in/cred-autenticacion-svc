package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.RolEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, UUID>, ReactiveQueryByExampleExecutor<RolEntity> {


    @Query("""
           SELECT r.id_rol, r.nombre, r.descripcion
           FROM roles r
           JOIN usuarios u ON u.id_rol = r.id_rol
           WHERE u.id = :userId
           """)
    Flux<RolEntity> findByUserId(UUID userId);

    Mono<RolEntity> findByName(String name);
}