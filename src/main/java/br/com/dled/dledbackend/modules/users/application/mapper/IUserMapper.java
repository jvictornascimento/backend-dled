package br.com.dled.dledbackend.modules.users.application.mapper;

import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    UserDto fromOut(UserAccount user);
}
