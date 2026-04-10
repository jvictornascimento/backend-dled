package br.com.dled.dledbackend.modules.users.application;

import br.com.dled.dledbackend.modules.users.application.dto.UserCreateDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserUpdateDto;
import br.com.dled.dledbackend.modules.users.application.exception.UserEmailAlreadyExistsException;
import br.com.dled.dledbackend.modules.users.application.exception.UserNotFoundException;
import br.com.dled.dledbackend.modules.users.application.exception.UsernameAlreadyExistsException;
import br.com.dled.dledbackend.modules.users.application.mapper.IUserMapper;
import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import br.com.dled.dledbackend.modules.users.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.USER_EMAIL_ALREADY_EXISTS;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.USERNAME_ALREADY_EXISTS;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository repository;
    private final IUserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAll() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(mapper::fromOut)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        return mapper.fromOut(findById(userId));
    }

    @Override
    public UserDto create(UserCreateDto input) {
        validateUniqueFields(input.username(), input.email());

        UserAccount user = new UserAccount();
        applyCreateInput(user, input);
        return mapper.fromOut(repository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserUpdateDto input) {
        UserAccount user = findById(userId);
        validateUniqueFields(input.username(), input.email(), userId);

        applyUpdateInput(user, input);
        return mapper.fromOut(repository.save(user));
    }

    @Override
    public void delete(Long userId) {
        repository.delete(findById(userId));
    }

    private UserAccount findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.getMassage()));
    }

    private void validateUniqueFields(String username, String email) {
        if (repository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyExistsException(USERNAME_ALREADY_EXISTS.getMassage());
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new UserEmailAlreadyExistsException(USER_EMAIL_ALREADY_EXISTS.getMassage());
        }
    }

    private void validateUniqueFields(String username, String email, Long userId) {
        if (repository.existsByUsernameIgnoreCaseAndIdNot(username, userId)) {
            throw new UsernameAlreadyExistsException(USERNAME_ALREADY_EXISTS.getMassage());
        }
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, userId)) {
            throw new UserEmailAlreadyExistsException(USER_EMAIL_ALREADY_EXISTS.getMassage());
        }
    }

    private void applyCreateInput(UserAccount user, UserCreateDto input) {
        user.setFullName(input.fullName());
        user.setUsername(input.username());
        user.setEmail(input.email());
        user.setPhone(input.phone());
        user.setCompanyName(input.companyName());
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setRole(input.role());
        user.setActive(input.active());
    }

    private void applyUpdateInput(UserAccount user, UserUpdateDto input) {
        user.setFullName(input.fullName());
        user.setUsername(input.username());
        user.setEmail(input.email());
        user.setPhone(input.phone());
        user.setCompanyName(input.companyName());
        user.setRole(input.role());
        user.setActive(input.active());

        if (input.password() != null && !input.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(input.password()));
        }
    }
}
