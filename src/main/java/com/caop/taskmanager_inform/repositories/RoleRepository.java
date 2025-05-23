package com.caop.taskmanager_inform.repositories;

import com.caop.taskmanager_inform.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
