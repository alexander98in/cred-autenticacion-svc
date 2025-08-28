package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository> implements UserRepository {

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> findByDocumentId(String documentId) {
        return repository.findByDocumentId(documentId)
            .map(this::toEntity);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
            .map(this::toEntity);
    }

    @Override
    public Mono<User> findUserById(UUID id) {
        return repository.findById(id)
            .map(this::toEntity);
    }

    @Override
    public Mono<User> register(User user) {
        return repository.save(toData(user))
            .map(this::toEntity);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByDocumentId(String documentId) {
        return repository.existsByDocumentId(documentId);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Flux<User> findAllUsers() {
        return repository.findAll()
            .map(this::toEntity);
    }
}
