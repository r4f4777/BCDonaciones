package com.tfg.backend.dto;

import java.util.Set;

public class LoginResponse {

    private String token;
    private String username;
    private Set<String> roles;

    // Constructor “full args”
    public LoginResponse(String token, Set<String> roles, String username) {
        this.token = token;
        this.roles = roles;
        this.username = username;
    }

    // Constructor vacío (para Jackson)
    public LoginResponse() {}

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public Set<String> getRoles() {
        return roles;
    }
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
