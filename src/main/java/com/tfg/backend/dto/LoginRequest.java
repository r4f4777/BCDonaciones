package com.tfg.backend.dto;

import com.tfg.backend.model.Rol;

public class LoginRequest {
    private String email;
    private String password;
    private Rol role;

    // Getters y Setters

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
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
}
