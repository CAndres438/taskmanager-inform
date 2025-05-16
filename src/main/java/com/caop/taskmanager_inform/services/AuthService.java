package com.caop.taskmanager_inform.services;

import com.caop.taskmanager_inform.dto.AuthRequest;
import com.caop.taskmanager_inform.dto.AuthResponse;
import com.caop.taskmanager_inform.models.Role;
import com.caop.taskmanager_inform.models.User;
import com.caop.taskmanager_inform.repositories.RoleRepository;
import com.caop.taskmanager_inform.repositories.UserRepository;
import com.caop.taskmanager_inform.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class AuthService implements IAuthService{
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("auth.user_not_found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("auth.invalid_credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String name = user.getName();
        String email = user.getEmail();
        String role = user.getRoles().iterator().next().getName();

        return new AuthResponse(token, name, email, role);
    }

    @Override
    public AuthResponse register(AuthRequest request) {
            if (userRepo.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("auth.email_already_registered");
            }

            Role defaultRole = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRoles(Set.of(defaultRole));

            userRepo.save(newUser);

            String token = jwtUtil.generateToken(newUser.getEmail());
            String name = newUser.getName();
            String email = newUser.getEmail();
            String role = newUser.getRoles().iterator().next().getName();

            return new AuthResponse(token, name, email, role);
    }
}
