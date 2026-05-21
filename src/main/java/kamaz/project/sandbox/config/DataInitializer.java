package kamaz.project.sandbox.config;

import kamaz.project.sandbox.models.*;
import kamaz.project.sandbox.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepo.count() == 0) {
            Role adminRole = Role.builder().name("ADMIN").build();
            Role userRole = Role.builder().name("USER").build();
            roleRepo.saveAll(List.of(adminRole, userRole));
        }

        if (userRepo.count() == 0) {
            Role adminRole = roleRepo.findByName("ADMIN").orElseThrow();
            Role userRole = roleRepo.findByName("USER").orElseThrow();

            User admin = User.builder()
                .username("admin").password(encoder.encode("admin")).role(adminRole).build();
            User user = User.builder()
                .username("user").password(encoder.encode("user")).role(userRole).build();
            userRepo.saveAll(List.of(admin, user));
        }
    }
}