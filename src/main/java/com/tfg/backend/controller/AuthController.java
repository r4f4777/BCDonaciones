package com.tfg.backend.controller;

import com.tfg.backend.dto.LoginRequest;
import com.tfg.backend.dto.LoginResponse;
import com.tfg.backend.dto.RegisterRequest;
import com.tfg.backend.dto.SwitchRoleRequest;
import com.tfg.backend.dto.SwitchRoleResponse;
import com.tfg.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Registra un nuevo usuario y devuelve LoginResponse con token y datos.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            LoginResponse resp = authService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (RuntimeException ex) {
            // por ejemplo: email ya existe
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    /**
     * Login en dos fases:
     * 1) Si req.getRole()==null devuelve Set<String> de roles.
     * 2) Si request.getRole()!=null devuelve LoginResponse (token, roles, nombre).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Object result = authService.login(req);

        if (result instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<String> roles = (Set<String>) result;
            return ResponseEntity.ok(roles);
        } else {
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
        String token = bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : bearer;
        SwitchRoleResponse resp = authService.switchRole(token, req);
        return ResponseEntity.ok(resp);
    }
}
