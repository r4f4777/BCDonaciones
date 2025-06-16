package com.tfg.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {

    private PrivateKey privateKey;
    private PublicKey  publicKey;

    @Value("${security.jwt.expiration-time}")
    private final long expirationTime = 86400000; // 1 día en ms

    @PostConstruct
    public void init() {
        try {
            privateKey = loadPrivateKeyFromResource("/keys/private.pem");
            publicKey  = loadPublicKeyFromResource("/keys/public.pem");
            System.out.println("🔑 Claves RSA cargadas correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error cargando claves RSA", e);
        }
    }

    private PrivateKey loadPrivateKeyFromResource(String resourcePath) throws Exception {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("No se encontró la clave privada en: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String keyPem = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());
            byte[] decoded = Base64.getDecoder().decode(keyPem);
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        }
    }

    private PublicKey loadPublicKeyFromResource(String resourcePath) throws Exception {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("No se encontró la clave pública en: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String keyPem = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());
            byte[] decoded = Base64.getDecoder().decode(keyPem);
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
        }
    }

    /* Genera un JWT incluyendo id, nombre y roles */
    public String generateToken(
            String email,
            Set<String> roles,
            String nombre,
            Long id
    ) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .claim("nombre", nombre)
                .claim("id", id)                          // <-- incluimos el id
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        return extractClaim(token, c -> new HashSet<>(c.get("roles", List.class)));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
