package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.modules.users.domain.UserAccount;
import br.com.dled.dledbackend.modules.users.domain.UserRole;
import br.com.dled.dledbackend.modules.users.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(InitialAdminBootstrapProperties.class)
public class InitialAdminBootstrap implements ApplicationRunner {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$"
    );

    private final InitialAdminBootstrapProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            return;
        }

        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        validateProperties();

        UserAccount admin = new UserAccount();
        admin.setFullName(properties.getFullName());
        admin.setUsername(properties.getUsername());
        admin.setEmail(properties.getEmail());
        admin.setPhone(properties.getPhone());
        admin.setCompanyName(properties.getCompanyName());
        admin.setPassword(passwordEncoder.encode(properties.getPassword()));
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);
        log.info("Initial admin user created with username '{}'", properties.getUsername());
    }

    private void validateProperties() {
        List<String> missingProperties = new ArrayList<>();
        addMissingProperty(missingProperties, "INITIAL_ADMIN_FULL_NAME", properties.getFullName());
        addMissingProperty(missingProperties, "INITIAL_ADMIN_USERNAME", properties.getUsername());
        addMissingProperty(missingProperties, "INITIAL_ADMIN_EMAIL", properties.getEmail());
        addMissingProperty(missingProperties, "INITIAL_ADMIN_PHONE", properties.getPhone());
        addMissingProperty(missingProperties, "INITIAL_ADMIN_COMPANY_NAME", properties.getCompanyName());
        addMissingProperty(missingProperties, "INITIAL_ADMIN_PASSWORD", properties.getPassword());

        if (!missingProperties.isEmpty()) {
            throw new IllegalStateException(
                    "Initial admin bootstrap is enabled and no ADMIN user exists. Missing environment variables: "
                            + String.join(", ", missingProperties)
            );
        }

        if (!PASSWORD_PATTERN.matcher(properties.getPassword()).matches()) {
            throw new IllegalStateException(
                    "INITIAL_ADMIN_PASSWORD must be 8 to 20 characters long and contain uppercase, lowercase, number and special character."
            );
        }
    }

    private void addMissingProperty(List<String> missingProperties, String environmentVariable, String value) {
        if (!StringUtils.hasText(value)) {
            missingProperties.add(environmentVariable);
        }
    }
}
