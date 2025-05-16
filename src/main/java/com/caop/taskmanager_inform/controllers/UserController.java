package com.caop.taskmanager_inform.controllers;


import com.caop.taskmanager_inform.dto.UserResponse;
import com.caop.taskmanager_inform.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class UserController {
    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()))
                .toList();
    }
}
