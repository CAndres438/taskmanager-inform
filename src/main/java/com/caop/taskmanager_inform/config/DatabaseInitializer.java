package com.caop.taskmanager_inform.config;

import com.caop.taskmanager_inform.models.Role;
import com.caop.taskmanager_inform.models.User;
import com.caop.taskmanager_inform.repositories.RoleRepository;
import com.caop.taskmanager_inform.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init(){
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    log.info("Rol: No ROLE_ADMIN ROLE. Creating...");
                    return roleRepo.save(new Role(null, "ROLE_ADMIN"));
                });

        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> {
                    log.info("Rol: No ROLE_USER ROLE. Creating...");
                    return roleRepo.save(new Role(null, "ROLE_USER"));
                });

        // Create ROLE_ADMIN User
        if (userRepo.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setName("Andres O Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            userRepo.save(admin);
            log.info("User: ROLE_ADMIN created as admin@gmail.com / admin123");
        } else {
            log.info("User: admin user just exist");
        }

        // Create ROLE_User User
        if (userRepo.findByEmail("user@gmail.com").isEmpty()) {
            User user = new User();
            user.setName("Andres O User");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            userRepo.save(user);
            log.info("USER: ROLE_USER created as user@gmail.com / user123");
        } else {
            log.info("USER: user user just exist");
        }

    }
}
