package co.com.pragma.autenticacion.model.rol.gateways;

import co.com.pragma.autenticacion.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolRepository {

    Mono<Void> deleteRolById(UUID idRol);

    Mono<Rol> saveRol(Rol rol);

    Mono<Rol> findRolById(UUID idRol);

    Flux<Rol> findAllRols();

    Flux<Rol> findRolesByUserId(UUID userId);

    Mono<Rol> findRolByName(String name);
}
