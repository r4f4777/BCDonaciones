package com.tfg.backend.controller;

import com.tfg.backend.dto.CurrentUserResponse;
import com.tfg.backend.dto.JoinRequestDTO;
import com.tfg.backend.dto.UsuarioDTO;
import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.service.JoinRequestService;
import com.tfg.backend.service.UsuarioService;
import com.tfg.backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.tfg.backend.dto.CurrentUserResponse;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JoinRequestService jrService;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService,
                             JoinRequestService jrService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jrService = jrService;
        this.jwtService = jwtService;
    }



    @Operation(summary = "Obtener todos los usuarios",
            description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        List<UsuarioDTO> dtos = usuarios.stream()
                .map(usuarioService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un usuario por ID",
            description = "Busca un usuario en la base de datos usando su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UsuarioDTO dto = usuarioService.convertToDTO(usuario);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Eliminar un usuario por ID",
            description = "Elimina un usuario de la base de datos según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearer
    ) {
        // 1. Quitar prefijo "Bearer "
        String token = bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : bearer;

        // 2. Sacar datos del token
        String email      = jwtService.extractUsername(token);
        String nombre     = jwtService.extractClaim(token, c -> c.get("nombre", String.class));
        Set<String> roles = jwtService.extractRoles(token);
        String activeRole = roles.iterator().next();

        CurrentUserResponse resp = new CurrentUserResponse(
                null,       // id (opcional)
                nombre,
                email,
                activeRole,
                roles
        );

        return ResponseEntity.ok(resp);
    }


    @Operation(summary = "Actualizar perfil de usuario",
            description = "Permite a un usuario autenticado actualizar su información.")
    @PutMapping("/update")
    public ResponseEntity<UsuarioDTO> updateUsuario(@RequestBody UsuarioDTO usuarioDTO,
                                                    Authentication authentication) {
        String email = authentication.getName();
        Usuario updated = usuarioService.updateUsuario(email, usuarioDTO);
        UsuarioDTO updatedDTO = usuarioService.convertToDTO(updated);
        return ResponseEntity.ok(updatedDTO);
    }

    @PostMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('ONG','AYUNTAMIENTO')")
    public ResponseEntity<JoinRequestDTO> postSolicitud(
            @RequestParam Long entidadId,
            @RequestParam Long campaignId) {
        JoinRequestDTO dto = jrService.create(entidadId, campaignId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/solicitudes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JoinRequestDTO>> getSolicitudes() {
        return ResponseEntity.ok(jrService.listPending());
    }

    @PostMapping("/solicitudes/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveSolicitud(@PathVariable Long id) {
        jrService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/solicitudes/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectSolicitud(@PathVariable Long id) {
        jrService.reject(id);
        return ResponseEntity.ok().build();
    }
}
