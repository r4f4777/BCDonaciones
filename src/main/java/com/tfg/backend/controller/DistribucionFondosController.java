package com.tfg.backend.controller;

import com.tfg.backend.dto.DistribucionFondosDTO;
import com.tfg.backend.model.DistribucionFondos;
import com.tfg.backend.service.DistribucionFondosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Distribución de Fondos", description = "API para gestionar la distribución de fondos de las donaciones")
@RestController
@RequestMapping("/api/distribuciones")
public class DistribucionFondosController {

    private final DistribucionFondosService distribucionFondosService;

    public DistribucionFondosController(DistribucionFondosService distribucionFondosService) {
        this.distribucionFondosService = distribucionFondosService;
    }

    @Operation(summary = "Obtener todas las distribuciones de fondos", description = "Devuelve una lista con todas las distribuciones registradas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de distribuciones obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<DistribucionFondos>> getAllDistribuciones() {
        return ResponseEntity.ok(distribucionFondosService.getAllDistribuciones());
    }

    @Operation(summary = "Registrar una nueva distribución de fondos", description = "Registra una nueva distribución de fondos en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Distribución registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })

    @PostMapping
    public ResponseEntity<DistribucionFondos> createDistribucion(@RequestBody DistribucionFondosDTO dto) {
        DistribucionFondos distribucion = distribucionFondosService.createDistribucion(dto);
        return ResponseEntity.ok(distribucion);
    }
}
