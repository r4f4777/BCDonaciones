package com.tfg.backend.controller;

import com.tfg.backend.dto.EntidadReceptoraDTO;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.service.EntidadReceptoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Tag(name = "Entidades Receptoras", description = "API para la gestión de entidades receptoras de donaciones")
@RestController
@RequestMapping("/api/entidades")
public class EntidadReceptoraController {

    private final EntidadReceptoraService entidadReceptoraService;

    public EntidadReceptoraController(EntidadReceptoraService entidadReceptoraService) {
        this.entidadReceptoraService = entidadReceptoraService;
    }

    @Operation(summary = "Obtener todas las entidades receptoras", description = "Devuelve una lista con todas las entidades receptoras registradas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de entidades obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<EntidadReceptoraDTO>> getAllEntidades() {
        List<EntidadReceptora> entidades = entidadReceptoraService.getAllEntidades();
        List<EntidadReceptoraDTO> dtos = entidades.stream()
                .map(entidadReceptoraService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una entidad receptora por ID", description = "Busca una entidad en la base de datos usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entidad encontrada"),
            @ApiResponse(responseCode = "404", description = "Entidad no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntidadReceptoraDTO> getEntidadById(@PathVariable Long id) {
        Optional<EntidadReceptora> entidadOpt = entidadReceptoraService.getEntidadById(id);
        if (entidadOpt.isPresent()) {
            EntidadReceptoraDTO dto = entidadReceptoraService.convertToDTO(entidadOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener entidades receptoras por campaña", description = "Devuelve las entidades asociadas a una campaña específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entidades obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron entidades para la campaña")
    })
    @GetMapping("/campania/{id}")
    public ResponseEntity<List<EntidadReceptoraDTO>> getEntidadesByCampania(@PathVariable Long id) {
        List<EntidadReceptora> entidades = entidadReceptoraService.findByCampaniaId(id);
        List<EntidadReceptoraDTO> dtos = entidades.stream()
                .map(entidadReceptoraService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Eliminar una entidad receptora por ID", description = "Elimina una entidad receptora de la base de datos según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entidad eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Entidad no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntidad(@PathVariable Long id) {
        entidadReceptoraService.deleteEntidad(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener entidades receptoras paginadas", description = "Devuelve una página de entidades receptoras")
    @GetMapping("/paginadas")
    public ResponseEntity<Page<EntidadReceptoraDTO>> getEntidadesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<EntidadReceptoraDTO> pagedDtos = entidadReceptoraService.getEntidadesPaginadas(pageable)
                .map(entidadReceptoraService::convertToDTO);
        return ResponseEntity.ok(pagedDtos);
    }

}
