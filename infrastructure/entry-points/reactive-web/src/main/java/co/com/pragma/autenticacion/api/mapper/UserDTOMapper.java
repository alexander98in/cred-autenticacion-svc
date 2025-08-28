package co.com.pragma.autenticacion.api.mapper;

import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.UserResponseDTO;
import co.com.pragma.autenticacion.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idRol", expression = "java( (dto.idRol()==null || dto.idRol().isBlank()) ? null : UUID.fromString(dto.idRol()) )")
    User toDomain(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);
}
