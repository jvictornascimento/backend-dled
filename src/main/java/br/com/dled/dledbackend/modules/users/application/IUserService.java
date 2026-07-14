package br.com.dled.dledbackend.modules.users.application;

import br.com.dled.dledbackend.modules.users.application.dto.UserCreateDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserUpdateDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getAll();
    UserDto getById(Long userId);
    UserDto create(UserCreateDto input);
    UserDto update(Long userId, UserUpdateDto input);
    void delete(Long userId);
}
