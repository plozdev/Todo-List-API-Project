package plozdev.todolistapi.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.entities.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "passwordHash", source = "password")
    public abstract User toEntity(RegisterRequest request);

    @AfterMapping
    protected void encodePassword(RegisterRequest request,
                                  @MappingTarget User user) {
        if (request.getPassword() != null)
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    }

}
