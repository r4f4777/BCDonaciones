package com.tfg.backend.dto;

public record SwitchRoleResponse(
        String token,
        String activeRole
) {}
