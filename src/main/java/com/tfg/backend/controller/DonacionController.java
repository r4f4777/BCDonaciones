package com.tfg.backend.controller;

import com.tfg.backend.dto.DonacionDTO;
import com.tfg.backend.dto.DonacionResponseDTO;
import com.tfg.backend.model.Campania;
import com.tfg.backend.model.Donacion;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.CampaniaRepository;
import com.tfg.backend.repository.DonacionRepository;
import com.tfg.backend.repository.EntidadReceptoraRepository;
import com.tfg.backend.repository.UsuarioRepository;
import com.tfg.backend.service.DonacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Donaciones", description = "API para gestionar las donaciones")
@RestController
@RequestMapping("/api/donaciones")
public class DonacionController {

    private final DonacionService donacionService;
    private final UsuarioRepository usuarioRepository;
    private final DonacionRepository donacionRepository;
    private final EntidadReceptoraRepository entidadReceptoraRepository;
    private final CampaniaRepository campaniaRepository;
    private static final Logger logger = LoggerFactory.getLogger(DonacionController.class);

    public DonacionController(DonacionService donacionService,
                              UsuarioRepository usuarioRepository,
                              DonacionRepository donacionRepository,
                              EntidadReceptoraRepository entidadReceptoraRepository,
                              CampaniaRepository campaniaRepository) {
        this.donacionService = donacionService;
        this.usuarioRepository = usuarioRepository;
        this.donacionRepository = donacionRepository;
        this.entidadReceptoraRepository = entidadReceptoraRepository;
        this.campaniaRepository = campaniaRepository;
    }

    @Operation(summary = "Obtener todas las donaciones",
            description = "Devuelve la lista completa de donaciones realizadas en la plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de donaciones obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Donacion>> getAllDonaciones() {
        return ResponseEntity.ok(donacionService.getAllDonaciones());
    }

    @Operation(summary = "Obtener una donación por ID",
            description = "Busca una donación en la base de datos usando su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donación encontrada"),
            @ApiResponse(responseCode = "404", description = "Donación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Donacion> getDonacionById(@PathVariable Long id) {
        Optional<Donacion> donacion = donacionService.getDonacionById(id);
        return donacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva donación",
            description = "Permite registrar una nueva donación en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Donación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<?> crearDonacion(@RequestBody DonacionDTO donacionDTO,
                                           Principal principal) {
        // 1. Validaciones básicas de entrada
        if (donacionDTO.getEntidadReceptoraId() == null) {
            return ResponseEntity.badRequest().body("entidadReceptoraId es obligatorio");
        }
        if (donacionDTO.getCampaniaId() == null) {
            return ResponseEntity.badRequest().body("campaniaId es obligatorio");
        }

        try {
            // 2. Delegar toda la lógica al servicio
            // Ahora el servicio devolverá un DonacionResponseDTO
            DonacionResponseDTO responseDTO = donacionService.procesarNuevaDonacion(
                    donacionDTO, principal.getName()); // Pasa el email del donante

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (RuntimeException e) {
            // Manejo de errores a nivel de controlador para excepciones lanzadas por el servicio
            System.err.println("Error al procesar la donación: " + e.getMessage());
            // Devuelve el mensaje de la excepción RuntimeException (ej. "Fallo al registrar la donación en blockchain")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar la donación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno al procesar la donación.");
        }
    }

    @Operation(summary = "Obtener las donaciones del usuario autenticado",
            description = "Devuelve una lista de donaciones realizadas por el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donaciones obtenidas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/user")
    public ResponseEntity<List<DonacionDTO>> getDonationsByUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        List<DonacionDTO> dtos = donacionService
                .getDonationsByEmail(email)
                .stream()
                .map(donacionService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener las donaciones recibidas por la entidad receptora autenticada",
            description = "Devuelve una lista de donaciones dirigidas a la entidad receptora autenticada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donaciones recibidas obtenidas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido, sin permisos suficientes")
    })
    @GetMapping("/recibidas")
    public ResponseEntity<List<DonacionDTO>> getDonacionesRecibidas(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Logueamos los roles del usuario en lugar de un único rol
        Set<Rol> roles = usuario.getRoles();
        logger.debug("Roles del usuario: {}", roles);

        EntidadReceptora entidad = entidadReceptoraRepository
                .findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Entidad receptora no encontrada"));

        List<DonacionDTO> dtos = donacionRepository.findByEntidadReceptora(entidad)
                .stream()
                .map(donacionService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/campania/{id}")
    public ResponseEntity<List<DonacionDTO>> getDonacionesPorCampania(@PathVariable Long id) {
        List<DonacionDTO> dtos = donacionRepository.findByCampania_Id(id)
                .stream()
                .map(donacionService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
