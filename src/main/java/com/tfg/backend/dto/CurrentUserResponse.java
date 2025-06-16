// src/main/java/com/tfg/backend/dto/CurrentUserResponse.java
package com.tfg.backend.dto;

import java.util.Set;

public class CurrentUserResponse {
    private Long   id;
    private String nombre;
    private String email;
    private String activeRole;
    private Set<String> roles;

    public CurrentUserResponse() {}

    public CurrentUserResponse(Long id,
                               String nombre,
                               String email,
                               String activeRole,
                               Set<String> roles) {
        this.id         = id;
        this.nombre     = nombre;
        this.email      = email;
        this.activeRole = activeRole;
        this.roles      = roles;
    }

    // getters y setters...
}
