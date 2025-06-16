package com.tfg.backend.security;

import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.UsuarioRepository;
import com.tfg.backend.security.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public CustomOAuth2SuccessHandler(UsuarioRepository usuarioRepository,
                                      JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email  = oauthUser.getAttribute("email");
        String nombre = oauthUser.getAttribute("name");
        Long id = oauthUser.getAttribute("id");

        // Busca o crea el usuario en la BD, asignando rol DONANTE si es nuevo
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setEmail(email);
                    u.setNombre(nombre);
                    u.setPassword(""); // Sin password para OAuth2
                    u.setRoles(Set.of(Rol.DONANTE));
                    return usuarioRepository.save(u);
                });

        // Extrae los roles del usuario como Set<String>
        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::name)
                .collect(Collectors.toSet());

        // Genera el JWT con los roles y el nombre
        String jwtToken = jwtService.generateToken(email, roles, nombre, id);

        // Redirige al frontend con el token en la URL
        response.sendRedirect("http://localhost:4200/oauth2-success?token=" + jwtToken);
    }
}
