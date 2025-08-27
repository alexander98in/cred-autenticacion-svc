package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.r2dbc.entity.RolEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<Rol, RolEntity, UUID, RolReactiveRepository> implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, org.reactivecommons.utils.ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }


    @Override
    public Mono<Void> deleteById(UUID idRol) {
        return null;
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        return super.save(rol);
    }
}
