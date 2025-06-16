package com.tfg.backend.service;

import com.tfg.backend.dto.LoginRequest;
import com.tfg.backend.dto.LoginResponse;
import com.tfg.backend.dto.RegisterRequest;
import com.tfg.backend.dto.SwitchRoleRequest;
import com.tfg.backend.dto.SwitchRoleResponse;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.EntidadReceptoraRepository;
import com.tfg.backend.repository.UsuarioRepository;
import com.tfg.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioRepository userRepository;
    private final EntidadReceptoraRepository entidadReceptoraRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository userRepository,
                       EntidadReceptoraRepository entidadReceptoraRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.entidadReceptoraRepository = entidadReceptoraRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un usuario y devuelve su JWT
     */
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }
        Usuario u = new Usuario();
        u.setEmail(request.getEmail());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setNombre(request.getNombre());

        Set<Rol> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Rol.DONANTE);
        }
        u.setRoles(roles);
        userRepository.save(u);

        if (roles.contains(Rol.ONG) || roles.contains(Rol.AYUNTAMIENTO)) {
            EntidadReceptora ent = new EntidadReceptora();
            ent.setNombre(u.getNombre());
            ent.setTipo(roles.iterator().next().name());
            ent.setUsuario(u);
            entidadReceptoraRepository.save(ent);
        }

        Set<String> names = roles.stream()
                .map(Rol::name)
                .collect(Collectors.toSet());
        // Aquí ya pasamos el id
        String token = jwtService.generateToken(u.getEmail(), names, u.getNombre(), u.getId());
        return new LoginResponse(token, names, u.getNombre());
    }

    /**
     * Autentica credenciales y devuelve el UserDetails
     */
    public UserDetails authenticate(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return (UserDetails) auth.getPrincipal();
    }

    /**
     * Procesa un login en dos fases:
     *  - Si request.getRole()==null, devuelve Set<String> de roles
     *  - Si viene role, devuelve LoginResponse con JWT (ahora con id y nombre)
     */
    public Object login(LoginRequest request) {
        UserDetails ud = authenticate(request.getEmail(), request.getPassword());

        Set<String> actual = ud.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_",""))
                .collect(Collectors.toSet());

        if (request.getRole() == null) {
            return actual;
        }
        String desired = request.getRole().name();
        if (!actual.contains(desired)) {
            throw new RuntimeException("Rol no asignado: " + desired);
        }

        // Recuperamos el Usuario JPA para leer su id y nombre reales
        Usuario u = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(
                u.getEmail(),
                Set.of(desired),
                u.getNombre(),
                u.getId()
        );
        return new LoginResponse(token, actual, u.getNombre());
    }

    /**
     * Cambia el rol activo en un token existente y devuelve el nuevo JWT
     */
    public SwitchRoleResponse switchRole(String currentToken, SwitchRoleRequest req) {
        String email = jwtService.extractUsername(currentToken);
        Set<String> existing = jwtService.extractRoles(currentToken);
        String desired = req.getRole();

        if (!existing.contains(desired)) {
            throw new IllegalArgumentException("No tienes asignado el rol: " + desired);
        }

        // Extraemos nombre e id desde los claims del token
        String nombre = jwtService.extractClaim(currentToken, c -> c.get("nombre", String.class));
        Long id       = jwtService.extractClaim(currentToken, c -> c.get("id", Long.class));

        String token = jwtService.generateToken(
                email,
                Set.of(desired),
                nombre,
                id
        );
        return new SwitchRoleResponse(token, desired);
    }
}
