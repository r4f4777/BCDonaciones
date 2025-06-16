// src/main/java/com/tfg/backend/dto/AuthenticateResponse.java
package com.tfg.backend.dto;

import java.util.Set;

public class AuthenticateResponse {

    private String nombre;
    private Set<String> roles;

    public AuthenticateResponse() {}

    public AuthenticateResponse(String nombre, Set<String> roles) {
        this.nombre = nombre;
        this.roles = roles;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
