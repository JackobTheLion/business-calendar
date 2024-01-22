package ru.yakovlev.businesscalendar.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.model.user.UserRole;
import ru.yakovlev.businesscalendar.repository.UserRepository;
import ru.yakovlev.businesscalendar.repository.UserRoleRepository;

import java.util.List;

/**
 * Class is to create default administrator account.
 * Login and password can be set at deployment stage by indicating respective environmental variable.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAccountInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.login:admin}")
    private String login;

    @Value("${admin.password:admin}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        UserRole roleAdmin = userRoleRepository.findUserRoleByName("ROLE_ADMIN");

        User admin = User.builder()
                .userName(login)
                .email("no@email.com")
                .firstName("admin name")
                .lastName("admin name")
                .userRole(List.of(roleAdmin))
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build();
        try {
            userRepository.save(admin);
            log.error("--------------------------------------");
            log.error("Initial admin login '{}'.", login);
            log.error("Initial admin password '{}'.", password);
            log.error("--------------------------------------");
        } catch (DataIntegrityViolationException e) {
            log.error("Admin account already registered");
        }
    }
}
