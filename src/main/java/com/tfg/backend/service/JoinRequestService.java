package com.tfg.backend.service;

import com.tfg.backend.dto.JoinRequestDTO;
import com.tfg.backend.model.Campania;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.JoinRequest;
import com.tfg.backend.model.RequestStatus;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.CampaniaRepository;
import com.tfg.backend.repository.JoinRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JoinRequestService {
    private final JoinRequestRepository jrRepo;
    private final UsuarioService usuarioService;
    private final CampaniaRepository campRepo;

    public JoinRequestService(JoinRequestRepository jrRepo,
                              UsuarioService usuarioService,
                              CampaniaRepository campRepo) {
        this.jrRepo         = jrRepo;
        this.usuarioService = usuarioService;
        this.campRepo       = campRepo;
    }

    /**
     * Crea una solicitud de inscripción de una entidad a una campaña.
     */
    public JoinRequestDTO create(Long entidadId, Long campaignId) {
        Usuario u = usuarioService.getUsuarioById(entidadId)
                .orElseThrow(() -> new RuntimeException("Entidad no encontrada"));

        Campania c = campRepo.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada"));

        JoinRequest jr = new JoinRequest();
        jr.setEntidad(u);
        jr.setCampania(c);
        jr = jrRepo.save(jr);

        return toDTO(jr);
    }

    /**
     * Lista todas las solicitudes pendientes.
     */
    public List<JoinRequestDTO> listPending() {
        return jrRepo.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Aprueba una solicitud: marca el estado, asocia la entidad a la campaña
     * y graba la relación en campania_entidad.
     */
    @Transactional
    public void approve(Long id) {
        JoinRequest jr = jrRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        jr.setStatus(RequestStatus.APPROVED);
        jrRepo.save(jr);

        Campania campania = jr.getCampania();
        EntidadReceptora entidad = jr.getEntidad().getEntidadReceptora();
        // Asegurarse de no duplicar:
        if (!campania.getEntidadesReceptoras().contains(entidad)) {
            campania.getEntidadesReceptoras().add(entidad);
            campRepo.save(campania);
        }
    }

    /**
     * Rechaza una solicitud: marca el estado REJECTED.
     */
    public void reject(Long id) {
        JoinRequest jr = jrRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        jr.setStatus(RequestStatus.REJECTED);
        jrRepo.save(jr);
    }

    private JoinRequestDTO toDTO(JoinRequest jr) {
        JoinRequestDTO dto = new JoinRequestDTO();
        dto.setId(jr.getId());
        dto.setEntidadId(jr.getEntidad().getId());
        dto.setEntidadNombre(jr.getEntidad().getNombre());
        dto.setCampaignId(jr.getCampania().getId());
        dto.setStatus(jr.getStatus());
        dto.setRequestedAt(jr.getRequestedAt());
        return dto;
    }
}
