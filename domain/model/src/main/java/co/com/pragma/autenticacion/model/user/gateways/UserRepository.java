package co.com.pragma.autenticacion.model.user.gateways;

import co.com.pragma.autenticacion.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {

    /**
     * Guarda un usuario en el repositorio.
     *
     * @param user El usuario a guardar.
     * @return Un Mono que emite el usuario guardado.
     */
    Mono<User> save(User user);

    /**
     * Busca un usuario por su ID de documento.
     *
     * @param documentId El ID de documento del usuario a buscar.
     * @return Un Mono que emite el usuario encontrado, o vacío si no se encuentra.
     */
    Mono<User> findByDocumentId(String documentId);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario a buscar.
     * @return Un Mono que emite el usuario encontrado, o vacío si no se encuentra.
     */
    Mono<User> findByEmail(String email);

    /**
     * Busca un usuario por su ID.
     *
     * @param id El ID del usuario a buscar.
     * @return Un Mono que emite el usuario encontrado, o vacío si no se encuentra.
     */
    Mono<User> findById(UUID id);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @return Un Mono que indica la finalización de la operación.
     */
    Mono<Void> deleteById(UUID id);

    /**
     * Verifica si un usuario existe por su ID de documento.
     *
     * @param documentId El ID de documento del usuario a verificar.
     * @return Un Mono que emite true si el usuario existe, false en caso contrario.
     */
    Mono<Boolean> existsByDocumentId(String documentId);

    /**
     * Verifica si un usuario existe por su correo electrónico.
     *
     * @param email El correo electrónico del usuario a verificar.
     * @return Un Mono que emite true si el usuario existe, false en caso contrario.
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Busca todos los usuarios en el repositorio.
     *
     * @return Un Flux que emite todos los usuarios encontrados.
     */
    Flux<User> findAll();
}
