package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User/* change for domain model */,
        UserEntity/* change for adapter model */,
        UUID,
        MyReactiveRepository
> {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
    }

}
