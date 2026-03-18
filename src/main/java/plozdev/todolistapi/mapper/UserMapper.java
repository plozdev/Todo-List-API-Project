package plozdev.todolistapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "password")
    User toEntity(RegisterRequest request);

}
