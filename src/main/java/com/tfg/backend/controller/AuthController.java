package com.tfg.backend.controller;

import com.tfg.backend.dto.*;
import com.tfg.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login en dos fases:
     * 1) Si request.getRole()==null devuelve Set<String> de roles.
     * 2) Si request.getRole()!=null devuelve LoginResponse (token, roles, nombre).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Object result = authService.login(req);

        if (result instanceof Set) {
            // Fase 1: devolvemos únicamente el conjunto de roles
            @SuppressWarnings("unchecked")
            Set<String> roles = (Set<String>) result;
            return ResponseEntity.ok(roles);
        } else {
            // Fase 2: devolvemos el LoginResponse con el token JWT
            LoginResponse resp = (LoginResponse) result;
            return ResponseEntity.ok(resp);
        }
    }

    /**
     * Cambia el rol activo en el token y devuelve el nuevo JWT.
     */
    @PostMapping("/switch-role")
    public ResponseEntity<SwitchRoleResponse> switchRole(
            @RequestHeader("Authorization") String bearer,
            @RequestBody SwitchRoleRequest req
    ) {
        // Extraemos el token del header "Bearer ..."
        String token = bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : bearer;

        SwitchRoleResponse resp = authService.switchRole(token, req);
        return ResponseEntity.ok(resp);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * Recibe un RegisterRequest y devuelve un LoginResponse con el JWT.
     */
    @PostMapping("/register") // <-- ¡NUEVO ENDPOINT DE REGISTRO!
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest req) {
        LoginResponse resp = authService.register(req);
        return ResponseEntity.ok(resp);
    }
}
