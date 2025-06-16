package com.tfg.backend.controller;

import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.UsuarioRepository;
import com.tfg.backend.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class OAuth2LoginController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public OAuth2LoginController(UsuarioRepository usuarioRepository,
                                 JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/oauth2-success")
    public String onOauth2Success(Authentication authentication) {
        var oauth = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
        String email  = oauth.getPrincipal().getAttribute("email");
        String nombre = oauth.getPrincipal().getAttribute("name");

        // Busca o crea el usuario
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setEmail(email);
                    u.setNombre(nombre != null ? nombre : "Usuario OAuth2");
                    // Asignamos rol por defecto
                    u.setRoles(Set.of(Rol.DONANTE));
                    return usuarioRepository.save(u);
                });

        // Preparamos roles para el token
        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::name)
                .collect(Collectors.toSet());

        // Generamos JWT
        String token = jwtService.generateToken(email, roles, usuario.getNombre(), usuario.getId());

        // Redirigimos al frontend con el token como query param
        return "redirect:http://localhost:4200/oauth2-success?token=" + token;
    }
}
