package com.caop.taskmanager_inform.models;


import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    public Role() {
    }

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
