package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import br.com.dled.dledbackend.modules.users.domain.UserRole;
import br.com.dled.dledbackend.modules.users.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialAdminBootstrapTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateInitialAdminWhenEnabledAndNoAdminExists() {
        InitialAdminBootstrapProperties properties = validProperties();
        InitialAdminBootstrap bootstrap = new InitialAdminBootstrap(properties, userRepository, passwordEncoder);

        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);
        when(passwordEncoder.encode("Admin@123")).thenReturn("{bcrypt}encoded-password");

        bootstrap.run(null);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(captor.capture());

        UserAccount admin = captor.getValue();
        assertEquals("Administrador", admin.getFullName());
        assertEquals("admin", admin.getUsername());
        assertEquals("admin@dled.com", admin.getEmail());
        assertEquals("11999999999", admin.getPhone());
        assertEquals("DLED", admin.getCompanyName());
        assertEquals("{bcrypt}encoded-password", admin.getPassword());
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertTrue(admin.isActive());
    }

    @Test
    void shouldNotCreateInitialAdminWhenAdminAlreadyExists() {
        InitialAdminBootstrap bootstrap = new InitialAdminBootstrap(validProperties(), userRepository, passwordEncoder);

        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(true);

        bootstrap.run(null);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldNotCreateInitialAdminWhenDisabled() {
        InitialAdminBootstrapProperties properties = validProperties();
        properties.setEnabled(false);
        InitialAdminBootstrap bootstrap = new InitialAdminBootstrap(properties, userRepository, passwordEncoder);

        bootstrap.run(null);

        verify(userRepository, never()).existsByRole(UserRole.ADMIN);
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldFailWhenEnabledWithoutRequiredPropertiesAndNoAdminExists() {
        InitialAdminBootstrapProperties properties = validProperties();
        properties.setPassword("");
        InitialAdminBootstrap bootstrap = new InitialAdminBootstrap(properties, userRepository, passwordEncoder);

        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bootstrap.run(null));

        assertTrue(exception.getMessage().contains("INITIAL_ADMIN_PASSWORD"));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldFailWhenInitialAdminPasswordDoesNotFollowPolicy() {
        InitialAdminBootstrapProperties properties = validProperties();
        properties.setPassword("admin");
        InitialAdminBootstrap bootstrap = new InitialAdminBootstrap(properties, userRepository, passwordEncoder);

        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bootstrap.run(null));

        assertTrue(exception.getMessage().contains("INITIAL_ADMIN_PASSWORD must be 8 to 20 characters"));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private InitialAdminBootstrapProperties validProperties() {
        InitialAdminBootstrapProperties properties = new InitialAdminBootstrapProperties();
        properties.setEnabled(true);
        properties.setFullName("Administrador");
        properties.setUsername("admin");
        properties.setEmail("admin@dled.com");
        properties.setPhone("11999999999");
        properties.setCompanyName("DLED");
        properties.setPassword("Admin@123");
        return properties;
    }
}
