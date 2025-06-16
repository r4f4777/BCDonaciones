package com.tfg.backend.service;

import com.tfg.backend.dto.CampaniaDTO;
import com.tfg.backend.dto.EntidadReceptoraDTO;
import com.tfg.backend.model.Campania;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.JoinRequest;
import com.tfg.backend.model.RequestStatus;
import com.tfg.backend.repository.CampaniaRepository;
import com.tfg.backend.repository.EntidadReceptoraRepository;
import com.tfg.backend.repository.JoinRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CampaniaService {

    private final CampaniaRepository campaniaRepository;
    private final EntidadReceptoraRepository entidadReceptoraRepository;
    private final JoinRequestRepository joinRequestRepository;

    public CampaniaService(CampaniaRepository campaniaRepository,
                           EntidadReceptoraRepository entidadReceptoraRepository,
                           JoinRequestRepository joinRequestRepository) {
        this.campaniaRepository       = campaniaRepository;
        this.entidadReceptoraRepository = entidadReceptoraRepository;
        this.joinRequestRepository    = joinRequestRepository;
    }

    /** 1) Todas las campañas */
    public List<Campania> getAllCampanias() {
        return campaniaRepository.findAll();
    }

    /** 2) Buscar por ID */
    public Optional<Campania> getCampaniaById(Long id) {
        return campaniaRepository.findById(id);
    }

    /** 3) Crear nueva campaña y auto-aprobar inscripción de la entidad */
    @Transactional
    public Campania createCampania(CampaniaDTO dto) {
        Campania c = new Campania();
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setMetaRecaudacion(dto.getMetaRecaudacion());

        if (dto.getEntidadReceptoraId() != null) {
            EntidadReceptora er = entidadReceptoraRepository
                    .findById(dto.getEntidadReceptoraId())
                    .orElseThrow(() -> new RuntimeException("Entidad no encontrada"));
            c.setEntidadReceptora(er);
        }

        c = campaniaRepository.save(c);

        // Inscripción automática y aprobada
        JoinRequest jr = new JoinRequest();
        jr.setCampania(c);
        jr.setEntidad(c.getEntidadReceptora().getUsuario());
        jr.setStatus(RequestStatus.APPROVED);
        jr.setRequestedAt(LocalDateTime.now());
        joinRequestRepository.save(jr);

        // Mantener la bidireccionalidad en memoria
        c.getJoinRequests().add(jr);

        return c;
    }

    /** 4) Eliminar campaña */
    public void deleteCampania(Long id) {
        campaniaRepository.deleteById(id);
    }

    /** 5) Convertir entidad JPA a DTO */
    public CampaniaDTO convertToDTO(Campania c) {
        CampaniaDTO dto = new CampaniaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setMetaRecaudacion(c.getMetaRecaudacion());

        if (c.getEntidadReceptora() != null) {
            dto.setEntidadReceptoraId(c.getEntidadReceptora().getId());
            dto.setEntidadReceptoraNombre(c.getEntidadReceptora().getNombre());
        }

        double total = c.getDonaciones().stream()
                .mapToDouble(d -> d.getMonto())
                .sum();
        dto.setTotalRecaudado(total);
        dto.setTotalDonaciones(c.getDonaciones().size());
        dto.setPorcentajeAlcanzado(
                dto.getMetaRecaudacion() != null && dto.getMetaRecaudacion() > 0
                        ? Math.min((total / dto.getMetaRecaudacion()) * 100, 100)
                        : 0.0
        );

        return dto;
    }

    /** 6) Lista de DTOs */
    public List<CampaniaDTO> convertToDTOList(List<Campania> campanias) {
        return campanias.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /** 7) Campañas donde ha donado un usuario */
    public List<Campania> getCampaignsByUser(String email) {
        return campaniaRepository.findDistinctByDonacionesUsuarioEmail(email);
    }

    /** 8) Asociar varias entidades (auto-aprobando cada JoinRequest) */
    @Transactional
    public Campania addEntidadesToCampania(Long campaniaId, List<Long> entidadIds) {
        Campania campania = campaniaRepository.findById(campaniaId)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada"));

        for (Long erId : entidadIds) {
            EntidadReceptora er = entidadReceptoraRepository.findById(erId)
                    .orElseThrow(() -> new RuntimeException("Entidad no encontrada"));
            JoinRequest jr = new JoinRequest();
            jr.setEntidad(er.getUsuario());
            jr.setCampania(campania);
            jr.setStatus(RequestStatus.APPROVED);
            joinRequestRepository.save(jr);
            campania.getJoinRequests().add(jr);
            // además de JR, la añadimos a la lista de M-N:
            if (!campania.getEntidadesReceptoras().contains(er)) {
                campania.getEntidadesReceptoras().add(er);
            }
        }

        return campaniaRepository.save(campania);
    }

    /** 9) Campañas paginadas */
    public Page<Campania> getCampaniasPaginadas(Pageable pageable) {
        return campaniaRepository.findAll(pageable);
    }

    /** 10) Obtener entidades receptoras asociadas de una campaña */
    public List<EntidadReceptora> getEntidadesDeCampania(Long campaniaId) {
        return campaniaRepository.findById(campaniaId)
                .map(Campania::getEntidadesReceptoras)
                .orElse(Collections.emptyList());
    }

    /** 11) Convertir EntidadReceptora a DTO */
    public EntidadReceptoraDTO convertEntidadToDTO(EntidadReceptora entidad) {
        EntidadReceptoraDTO dto = new EntidadReceptoraDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setTipo(entidad.getTipo());
        return dto;
    }
}
