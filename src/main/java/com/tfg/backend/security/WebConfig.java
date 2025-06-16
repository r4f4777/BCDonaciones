package com.tfg.backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para permitir peticiones
 * desde el frontend en localhost:4200 (Angular).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                          // aplica a todas las rutas
                .allowedOrigins("http://localhost:4200")    // origen permitido
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                       // cabeceras permitidas
                .exposedHeaders("Authorization")           // exponemos Authorization
                .allowCredentials(true)                    // habilita envío de cookies / Authorization
                .maxAge(3600);                             // cache de preflight en segundos
    }
}
