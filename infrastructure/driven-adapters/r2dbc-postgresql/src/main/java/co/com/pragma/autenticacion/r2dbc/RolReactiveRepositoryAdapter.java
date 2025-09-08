package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.r2dbc.entity.RolEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<Rol, RolEntity, UUID, RolReactiveRepository> implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, org.reactivecommons.utils.ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Void> deleteRolById(UUID idRol) {
        return repository.deleteById(idRol);
    }

    @Override
    public Mono<Rol> saveRol(Rol rol) {
        return repository.save(toData(rol))
            .map(this::toEntity);
    }

    @Override
    public Mono<Rol> findRolById(UUID idRol) {
        return repository.findById(idRol)
            .map(this::toEntity);
    }

    @Override
    public Flux<Rol> findAllRols() {
        return repository.findAll()
            .map(this::toEntity);
    }

    @Override
    public Flux<Rol> findRolesByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(this::toEntity);
    }
}
