package com.tfg.backend.dto;

import com.tfg.backend.model.Rol;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String nombre;
    private String password;
    private String email;
    private Set<Rol> roles;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String username) {
        this.nombre = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

}
