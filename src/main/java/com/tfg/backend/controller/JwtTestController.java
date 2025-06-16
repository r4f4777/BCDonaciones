package com.tfg.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/debug")
public class JwtTestController {

    @GetMapping("/jwt")
    public String checkJwt(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "No se recibió ningún token JWT válido en el encabezado Authorization.";
        }
        return "Token recibido: " + authHeader.substring(7);
    }
}

