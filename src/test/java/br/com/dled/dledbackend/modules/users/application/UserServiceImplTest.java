package br.com.dled.dledbackend.modules.users.application;

import br.com.dled.dledbackend.modules.users.application.dto.UserCreateDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserDto;
import br.com.dled.dledbackend.modules.users.application.dto.UserUpdateDto;
import br.com.dled.dledbackend.modules.users.application.exception.UserEmailAlreadyExistsException;
import br.com.dled.dledbackend.modules.users.application.exception.UserNotFoundException;
import br.com.dled.dledbackend.modules.users.application.exception.UsernameAlreadyExistsException;
import br.com.dled.dledbackend.modules.users.application.mapper.IUserMapper;
import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import br.com.dled.dledbackend.modules.users.domain.UserRole;
import br.com.dled.dledbackend.modules.users.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private IUserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void shouldReturnMappedUsers() {
        UserAccount user = createUser(1L, "Administrador", "admin", "admin@dled.com", UserRole.ADMIN);
        UserDto dto = createDto(1L, "Administrador", "admin", "admin@dled.com", UserRole.ADMIN);

        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(user));
        when(mapper.fromOut(user)).thenReturn(dto);

        List<UserDto> result = service.getAll();

        assertEquals(List.of(dto), result);
    }

    @Test
    void shouldReturnUserById() {
        UserAccount user = createUser(1L, "Comercial", "seller", "seller@dled.com", UserRole.SELLER);
        UserDto dto = createDto(1L, "Comercial", "seller", "seller@dled.com", UserRole.SELLER);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.fromOut(user)).thenReturn(dto);

        UserDto result = service.getById(1L);

        assertSame(dto, result);
    }

    @Test
    void shouldCreateUserEncodingPassword() {
        UserCreateDto input = new UserCreateDto(
                "Administrador",
                "admin",
                "admin@dled.com",
                "11999999999",
                "DLED Comercial",
                "Admin@123",
                UserRole.ADMIN,
                true
        );
        UserAccount saved = createUser(1L, "Administrador", "admin", "admin@dled.com", UserRole.ADMIN);
        UserDto dto = createDto(1L, "Administrador", "admin", "admin@dled.com", UserRole.ADMIN);

        when(repository.existsByUsernameIgnoreCase("admin")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("admin@dled.com")).thenReturn(false);
        when(passwordEncoder.encode("Admin@123")).thenReturn("encoded-password");
        when(repository.save(any(UserAccount.class))).thenReturn(saved);
        when(mapper.fromOut(saved)).thenReturn(dto);

        UserDto result = service.create(input);

        assertSame(dto, result);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(repository).save(captor.capture());
        assertEquals("encoded-password", captor.getValue().getPassword());
    }

    @Test
    void shouldUpdateUserAndKeepPasswordWhenNotProvided() {
        UserAccount existing = createUser(1L, "Cliente", "client", "client@dled.com", UserRole.CLIENT);
        existing.setPassword("stored-password");
        UserUpdateDto input = new UserUpdateDto(
                "Cliente Atualizado",
                "client",
                "new-client@dled.com",
                "11888888888",
                "Cliente VIP",
                null,
                UserRole.CLIENT,
                true
        );
        UserDto dto = createDto(1L, "Cliente Atualizado", "client", "new-client@dled.com", UserRole.CLIENT);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByUsernameIgnoreCaseAndIdNot("client", 1L)).thenReturn(false);
        when(repository.existsByEmailIgnoreCaseAndIdNot("new-client@dled.com", 1L)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.fromOut(existing)).thenReturn(dto);

        UserDto result = service.update(1L, input);

        assertSame(dto, result);
        assertEquals("stored-password", existing.getPassword());
        assertEquals("Cliente Atualizado", existing.getFullName());
    }

    @Test
    void shouldUpdateUserPasswordWhenProvided() {
        UserAccount existing = createUser(1L, "Usuario", "user", "user@dled.com", UserRole.USER);
        existing.setPassword("old-password");
        UserUpdateDto input = new UserUpdateDto(
                "Usuario",
                "user",
                "user@dled.com",
                "11777777777",
                "DLED",
                "NewUser@123",
                UserRole.USER,
                true
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByUsernameIgnoreCaseAndIdNot("user", 1L)).thenReturn(false);
        when(repository.existsByEmailIgnoreCaseAndIdNot("user@dled.com", 1L)).thenReturn(false);
        when(passwordEncoder.encode("NewUser@123")).thenReturn("new-password");
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.fromOut(existing)).thenReturn(createDto(1L, "Usuario", "user", "user@dled.com", UserRole.USER));

        service.update(1L, input);

        assertEquals("new-password", existing.getPassword());
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        UserCreateDto input = new UserCreateDto(
                "Administrador",
                "admin",
                "admin@dled.com",
                "11999999999",
                "DLED",
                "Admin@123",
                UserRole.ADMIN,
                true
        );

        when(repository.existsByUsernameIgnoreCase("admin")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> service.create(input));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        UserCreateDto input = new UserCreateDto(
                "Administrador",
                "admin",
                "admin@dled.com",
                "11999999999",
                "DLED",
                "Admin@123",
                UserRole.ADMIN,
                true
        );

        when(repository.existsByUsernameIgnoreCase("admin")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("admin@dled.com")).thenReturn(true);

        assertThrows(UserEmailAlreadyExistsException.class, () -> service.create(input));
    }

    @Test
    void shouldDeleteUser() {
        UserAccount user = createUser(1L, "Administrador", "admin", "admin@dled.com", UserRole.ADMIN);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).delete(user);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getById(1L));
        assertThrows(UserNotFoundException.class, () -> service.update(1L, new UserUpdateDto(
                "Usuario",
                "user",
                "user@dled.com",
                "11999999999",
                "DLED",
                null,
                UserRole.USER,
                true
        )));
        assertThrows(UserNotFoundException.class, () -> service.delete(1L));
    }

    private UserAccount createUser(Long id, String fullName, String username, String email, UserRole role) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone("11999999999");
        user.setCompanyName("DLED");
        user.setPassword("encoded-password");
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.of(2026, 4, 10, 9, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 4, 10, 9, 30));
        return user;
    }

    private UserDto createDto(Long id, String fullName, String username, String email, UserRole role) {
        return new UserDto(
                id,
                fullName,
                username,
                email,
                "11999999999",
                "DLED",
                role,
                true,
                LocalDateTime.of(2026, 4, 10, 9, 0),
                LocalDateTime.of(2026, 4, 10, 9, 30)
        );
    }
}
