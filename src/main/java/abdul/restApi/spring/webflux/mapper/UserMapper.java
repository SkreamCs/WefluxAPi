package abdul.restApi.spring.webflux.mapper;

import abdul.restApi.spring.webflux.dto.UserDto;
import abdul.restApi.spring.webflux.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(User user);

    @InheritInverseConfiguration
    User map(UserDto userDto);
}
