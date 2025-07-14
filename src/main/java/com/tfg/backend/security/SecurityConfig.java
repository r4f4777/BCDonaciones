package com.tfg.backend.security;

import com.tfg.backend.model.Rol;
import com.tfg.backend.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl    userDetailsService;
    private final JwtService                jwtService;
    private final UsuarioRepository         usuarioRepository;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsServiceImpl userDetailsService,
            JwtService jwtService,
            UsuarioRepository usuarioRepository
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService      = userDetailsService;
        this.jwtService              = jwtService;
        this.usuarioRepository       = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Habilitamos CORS con la fuente que definimos más abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2) Desactivamos CSRF (stateless JWT)
                .csrf(csrf -> csrf.disable())
                // 3) Configuración de rutas públicas vs protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/api/blockchain/**").permitAll()
                        // ---- PÚBLICAS: login, register, oauth2 y cambio de rol ----
                        .requestMatchers(HttpMethod.OPTIONS, "/api/auth/**").permitAll()        // preflight
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/switch-role").permitAll()
                        .requestMatchers("/api/auth/oauth2/**", "/oauth2-success").permitAll()

                        // ---- PÚBLICAS: consulta de campañas y entidades ----
                        .requestMatchers(HttpMethod.GET,"/api/blockchain/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campanias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/entidades/**").permitAll()

                        // ---- RUTAS PROTEGIDAS POR ROL ----
                        .requestMatchers(HttpMethod.POST,   "/api/donaciones").hasRole("DONANTE")
                        .requestMatchers(HttpMethod.GET,    "/api/donaciones/recibidas").hasAnyRole("ONG","AYUNTAMIENTO")
                        .requestMatchers(HttpMethod.POST,   "/api/usuarios/solicitudes").hasAnyRole("ONG","AYUNTAMIENTO")
                        .requestMatchers(HttpMethod.GET,    "/api/usuarios/solicitudes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/usuarios/solicitudes/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/usuarios/solicitudes/*/reject").hasRole("ADMIN")

                        // ---- CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN ----
                        .anyRequest().authenticated()
                )
                // 4) Si no está autenticado, devolvemos 401
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // 5) Stateless sessions
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 6) OAuth2 login → redirige al front con el token
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google")
                        .successHandler(this::onOAuth2Success)
                )
                // 7) Filtrado JWT + provider + userDetails
                .userDetailsService(userDetailsService)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void onOAuth2Success(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) throws IOException {
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        var attrs = oauth.getPrincipal().getAttributes();
        String email  = (String) attrs.get("email");
        String nombre = (String) attrs.getOrDefault("name", email);
        Long id = usuarioRepository.findByEmail(email).map(u -> u.getId()).orElse(null);

        Set<String> roles = usuarioRepository
                .findByEmail(email)
                .map(u -> u.getRoles().stream().map(Rol::name).collect(Collectors.toSet()))
                .orElse(Set.of(Rol.DONANTE.name()));

        String token = jwtService.generateToken(email, roles, nombre, id);
        response.sendRedirect("http://localhost:4200/oauth2-success?token=" + token);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de CORS para el SecurityFilterChain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
