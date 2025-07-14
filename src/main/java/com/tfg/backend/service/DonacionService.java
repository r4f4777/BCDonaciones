package com.tfg.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonacionService {

    private final DonacionRepository donacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Para convertir datosPago a JSON
    private final BlockchainService blockchainService;
    private final EntidadReceptoraRepository entidadReceptoraRepository;
    private final CampaniaRepository campaniaRepository;


    public DonacionService(DonacionRepository donacionRepository,
                           UsuarioRepository usuarioRepository, BlockchainService blockchainService, EntidadReceptoraRepository entidadReceptoraRepository, CampaniaRepository campaniaRepository) {
        this.donacionRepository = donacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.blockchainService = blockchainService;
        this.entidadReceptoraRepository = entidadReceptoraRepository;
        this.campaniaRepository = campaniaRepository;
    }

    public List<Donacion> getAllDonaciones() {
        return donacionRepository.findAll();
    }

    public Optional<Donacion> getDonacionById(Long id) {
        return donacionRepository.findById(id);
    }

    public List<Donacion> getDonacionesByUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        return (usuario != null)
                ? donacionRepository.findByUsuario(usuario)
                : List.of();
    }

    public Donacion createDonacion(Donacion donacion, Long donanteId) {
        Usuario usuario = usuarioRepository.findById(donanteId)
                .orElseThrow(() -> new RuntimeException("Donante no encontrado"));

        // Ahora comprobamos si tiene el rol DONANTE
        if (!usuario.getRoles().contains(Rol.DONANTE)) {
            throw new RuntimeException("El usuario no tiene rol de DONANTE");
        }

        donacion.setUsuario(usuario);
        return saveDonacion(donacion);
    }

    /**
     * Procesa una nueva donación: la guarda en la base de datos y en la blockchain.
     * Es el método que será llamado desde el controlador.
     */
    @Transactional
    public DonacionResponseDTO procesarNuevaDonacion(DonacionDTO donacionDTO, String donanteEmail) {
        // 1. Validar y obtener entidades de la base de datos relacional
        Usuario usuarioDonante = usuarioRepository.findByEmail(donanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario donante no encontrado con email: " + donanteEmail));

        EntidadReceptora entidadReceptora = entidadReceptoraRepository
                .findById(donacionDTO.getEntidadReceptoraId())
                .orElseThrow(() -> new RuntimeException("Entidad receptora no encontrada con ID: " + donacionDTO.getEntidadReceptoraId()));

        Campania campania = campaniaRepository.findById(donacionDTO.getCampaniaId())
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada con ID: " + donacionDTO.getCampaniaId()));

        // 2. Convertir DTO de entrada a entidad de dominio (Donacion)
        Donacion nuevaDonacion = convertToEntity(donacionDTO, usuarioDonante, entidadReceptora, campania);

        // 3. Guardar la donación en la base de datos relacional
        Donacion savedDonacion = donacionRepository.save(nuevaDonacion);
        System.out.println("Donación guardada en DB con ID: " + savedDonacion.getId());

        // 4. Preparar y registrar la donación en la blockchain
        try {
            // Generamos un UUID para el ID de la donación en la blockchain
            // Si tu chaincode usa el ID de la DB, puedes usar String.valueOf(savedDonacion.getId())
            String blockchainDonationID = UUID.randomUUID().toString(); // Usaremos UUID por simplicidad aquí

            // Los IDs de donante, campaña y entidad para la blockchain
            // Vienen del DTO de entrada, aseguramos que no sean null
            String donorID_bc = (donacionDTO.getDonanteID() != null) ? donacionDTO.getDonanteID() : usuarioDonante.getEmail(); // Fallback a email
            String campaignID_bc = (donacionDTO.getCampaniaID() != null) ? donacionDTO.getCampaniaID() : String.valueOf(campania.getId()); // Fallback a ID de DB
            String receiverID_bc = (donacionDTO.getEntidadID() != null) ? donacionDTO.getEntidadID() : String.valueOf(entidadReceptora.getId()); // Fallback a ID de DB

            // Monto como String para la blockchain
            // Aseguramos que el monto no sea null si es Double, y si es 0.0, lo convertimos bien.
            String amount_bc = (savedDonacion.getMonto() != null) ? String.valueOf(savedDonacion.getMonto()) : "0.0";


            // --- Llama al BlockchainService con los argumentos preparados ---
            blockchainService.createDonation(
                    blockchainDonationID, // ID de la donación para la blockchain
                    donorID_bc,
                    amount_bc,
                    campaignID_bc,
                    receiverID_bc
            );
            // -------------------------------------------------------------

            // 5. Convertir la entidad Donacion (savedDonacion) y los datos de blockchain
            // a DonacionResponseDTO para enviar la respuesta al frontend.
            return mapToDonacionResponseDTO(savedDonacion, blockchainDonationID);

        } catch (Exception e) {
            System.err.println("Error al procesar la donación (Blockchain): " + e.getMessage());
            throw new RuntimeException("Fallo al registrar la donación en blockchain: " + e.getMessage(), e);
        }
    }

    // Método para mapear la entidad Donacion a DonacionResponseDTO
    private DonacionResponseDTO mapToDonacionResponseDTO(Donacion donacion, String blockchainDonationID) {
        DonacionResponseDTO responseDTO = new DonacionResponseDTO();

        // Campos de la donación de la DB
        responseDTO.setId(blockchainDonationID); // Usa el ID que se envió a la blockchain
        responseDTO.setMonto(donacion.getMonto());
        responseDTO.setFecha(donacion.getFecha());

        // Para los campos String que esperaría la blockchain en DonacionResponseDTO (si los necesitas de vuelta)
        responseDTO.setDonanteID(donacion.getUsuario().getEmail()); // O algún otro ID de BC que uses
        responseDTO.setMetodoPago(donacion.getMetodoPago());

        // Campos relacionados de Entidad y Campaña (rellenar desde la DB)
        if (donacion.getEntidadReceptora() != null) {
            // responseDTO.setEntidadID(String.valueOf(donacion.getEntidadReceptora().getId())); // O su ID de blockchain
            responseDTO.setEntidadReceptoraNombre(donacion.getEntidadReceptora().getNombre());
        }
        if (donacion.getCampania() != null) {
            // responseDTO.setCampaniaID(String.valueOf(donacion.getCampania().getId())); // O su ID de blockchain
            responseDTO.setCampaniaNombre(donacion.getCampania().getNombre());
        }

        return responseDTO;
    }

    public List<Donacion> getDonationsByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return donacionRepository.findByUsuario(usuario);
    }

    public Donacion saveDonacion(Donacion donacion) {
        return donacionRepository.save(donacion);
    }

    public void deleteDonacion(Long id) {
        donacionRepository.deleteById(id);
    }

    public DonacionDTO convertToDTO(Donacion donacion) {
        DonacionDTO dto = new DonacionDTO();
        if (donacion.getId() != null) {
            dto.setId(String.valueOf(donacion.getId()));
        } else {
            dto.setId(null);
        }
        dto.setMonto(donacion.getMonto());
        dto.setFecha(donacion.getFecha().toString());
        dto.setMetodoPago(donacion.getMetodoPago());
        dto.setDatosPago(donacion.getDatosPago());

        if (donacion.getCampania() != null) {
            dto.setCampaniaId(donacion.getCampania().getId());
            dto.setCampaniaNombre(donacion.getCampania().getNombre());
        }
        if (donacion.getEntidadReceptora() != null) {
            dto.setEntidadReceptoraId(donacion.getEntidadReceptora().getId());
            dto.setEntidadReceptoraNombre(donacion.getEntidadReceptora().getNombre());
        }
        // Asegúrate de que si tu frontend espera donanteID, campaniaID, entidadID
        // al obtener la donación, estos campos se rellenen aquí.
        if (donacion.getUsuario() != null) {
            dto.setDonanteID(donacion.getUsuario().getEmail());
        }
        if (donacion.getCampania() != null) {
            dto.setCampaniaID(String.valueOf(donacion.getCampania().getId()));
        }
        if (donacion.getEntidadReceptora() != null) {
            dto.setEntidadID(String.valueOf(donacion.getEntidadReceptora().getId()));
        }

        return dto;
    }

    private Donacion convertToEntity(
            DonacionDTO dto, // Ahora dto.getFecha() devuelve String
            Usuario usuario,
            EntidadReceptora entidad,
            Campania campania
    ) {
        Donacion donacion = new Donacion();
        donacion.setMonto(dto.getMonto());

        // Manejo de la fecha: Ahora dto.getFecha() es un String
        if (dto.getFecha() != null && !dto.getFecha().isEmpty()) { // isEmpty() ahora es válido en String
            try {
                donacion.setFecha(LocalDate.parse(dto.getFecha())); // parse(String) es válido
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date from DTO: " + dto.getFecha() + " - " + e.getMessage());
                donacion.setFecha(LocalDate.now()); // Fallback
            }
        } else {
            donacion.setFecha(LocalDate.now()); // Si no se provee, usa la actual
        }

        donacion.setMetodoPago(dto.getMetodoPago());
        donacion.setDatosPago(dto.getDatosPago());

        donacion.setUsuario(usuario);
        donacion.setEntidadReceptora(entidad);
        donacion.setCampania(campania);
        return donacion;
    }

}
