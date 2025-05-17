package com.caop.taskmanager_inform.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class AuthRequest {
    @NotBlank(message = "auh.name_not_blank")
    private String name;

    @NotBlank(message = "auth.email_not_blank")
    @Email(message= "auth.email_invalid")
    private String email;

    @NotBlank(message = "auth.password_not_blank")
    @Size(min = 6, message = "auth.password_size")
    private String password;

    public AuthRequest(){}


    public AuthRequest(String email, String password, String name) {

        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}