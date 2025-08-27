package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.RolEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, UUID>, ReactiveQueryByExampleExecutor<RolEntity> {
}