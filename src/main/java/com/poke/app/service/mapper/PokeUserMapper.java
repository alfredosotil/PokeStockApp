package com.poke.app.service.mapper;

import com.poke.app.domain.PokeUser;
import com.poke.app.domain.User;
import com.poke.app.service.dto.PokeUserDTO;
import com.poke.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PokeUser} and its DTO {@link PokeUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface PokeUserMapper extends EntityMapper<PokeUserDTO, PokeUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    PokeUserDTO toDto(PokeUser s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
