package co.com.pragma.autenticacion.model.rol.gateways;

import co.com.pragma.autenticacion.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolRepository {

    /**
     * Elimina un rol por su ID.
     *
     * @param idRol El ID del rol a eliminar.
     * @return Un Mono que indica la finalización de la operación.
     */
    Mono<Void> deleteById(UUID idRol);

    /**
     * Guarda un rol en el repositorio.
     *
     * @param rol El rol a guardar.
     * @return Un Mono que emite el rol guardado.
     */
    Mono<Rol> save(Rol rol);

    /**
     * Busca un rol por su ID.
     *
     * @param idRol El ID del rol a buscar.
     * @return Un Mono que emite el rol encontrado, o vacío si no se encuentra.
     */
    Mono<Rol> findById(UUID idRol);

    /**
     * Busca todos los roles en el repositorio.
     *
     * @return Un Flux que emite todos los roles encontrados.
     */
    Flux<Rol> findAll();
}
