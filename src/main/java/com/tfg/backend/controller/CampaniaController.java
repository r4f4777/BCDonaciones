package com.tfg.backend.controller;

import com.tfg.backend.dto.CampaniaDTO;
import com.tfg.backend.dto.EntidadReceptoraDTO;
import com.tfg.backend.model.Campania;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.service.CampaniaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Tag(name = "Campañas", description = "API para la gestión de campañas de donación")
@RestController
@RequestMapping("/api/campanias")
public class CampaniaController {

    private final CampaniaService campaniaService;

    public CampaniaController(CampaniaService campaniaService) {
        this.campaniaService = campaniaService;
    }


    @Operation(summary = "Obtener todas las campañas", description = "Devuelve una lista con todas las campañas de donación registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping()
    public ResponseEntity<List<CampaniaDTO>> getAllCampanias() {
        List<Campania> campanias = campaniaService.getAllCampanias();
        List<CampaniaDTO> dtos = campaniaService.convertToDTOList(campanias);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una campaña por ID", description = "Busca una campaña en la base de datos usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaña encontrada"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CampaniaDTO> getCampaniaById(@PathVariable Long id) {
        Campania campania = campaniaService.getCampaniaById(id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada"));
        CampaniaDTO dto = campaniaService.convertToDTO(campania);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Crear una nueva campaña", description = "Registra una nueva campaña de donación en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campaña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<CampaniaDTO> createCampania(@RequestBody CampaniaDTO dto) {
        Campania campania = campaniaService.createCampania(dto);
        CampaniaDTO responseDto = campaniaService.convertToDTO(campania);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Eliminar una campaña por ID", description = "Elimina una campaña de la base de datos según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campaña eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampania(@PathVariable Long id) {
        campaniaService.deleteCampania(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener campañas por usuario",
            description = "Devuelve una lista de campañas en las que ha participado el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campañas obtenidas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/user")
    public ResponseEntity<List<CampaniaDTO>> getCampaignsByUser(Authentication authentication) {
        String email = authentication.getName();
        List<Campania> campanias = campaniaService.getCampaignsByUser(email);
        List<CampaniaDTO> dtos = campaniaService.convertToDTOList(campanias);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Asociar entidades a una campaña", description = "Permite vincular entidades receptoras a una campaña existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entidades asociadas correctamente"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    @PostMapping("/{id}/entidades")
    public ResponseEntity<CampaniaDTO> addEntidadesToCampania(
            @PathVariable Long id,
            @RequestBody List<Long> entidadIds) {

        Campania campania = campaniaService.addEntidadesToCampania(id, entidadIds);
        CampaniaDTO dto = campaniaService.convertToDTO(campania);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Obtener campañas paginadas", description = "Devuelve una página de campañas activas")
    @GetMapping("/paginadas")
    public ResponseEntity<Page<CampaniaDTO>> getCampaniasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<CampaniaDTO> pagedDtos = campaniaService.getCampaniasPaginadas(pageable)
                .map(campaniaService::convertToDTO);
        return ResponseEntity.ok(pagedDtos);
    }

    /**
     * GET /api/campanias/{id}/entidades
     * Devuelve las entidades receptoras aprobadas para esta campaña
     */
    @GetMapping("/{id}/entidades")
    /*public ResponseEntity<List<EntidadReceptoraDTO>> getEntidadesDeCampania(@PathVariable Long id) {
        List<EntidadReceptoraDTO> dtos = campaniaService.getEntidadesDeCampania(id)
                .stream()
                .map(campaniaService::convertEntidadToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }*/
    public ResponseEntity<List<EntidadReceptoraDTO>> getEntidadesDeCampania(@PathVariable Long id) {
        List<EntidadReceptoraDTO> dtos = campaniaService
                .getEntidadesDeCampania(id)
                .stream()
                .map(campaniaService::convertEntidadToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


}
