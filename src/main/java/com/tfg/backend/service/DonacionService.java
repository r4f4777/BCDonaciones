package com.tfg.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.dto.DonacionDTO;
import com.tfg.backend.model.Campania;
import com.tfg.backend.model.Donacion;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.Rol;
import com.tfg.backend.model.Usuario;
import com.tfg.backend.repository.DonacionRepository;
import com.tfg.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonacionService {

    private final DonacionRepository donacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Para convertir datosPago a JSON

    public DonacionService(DonacionRepository donacionRepository,
                           UsuarioRepository usuarioRepository) {
        this.donacionRepository = donacionRepository;
        this.usuarioRepository = usuarioRepository;
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
        dto.setId(donacion.getId());
        dto.setMonto(donacion.getMonto());
        dto.setFecha(donacion.getFecha());
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

        return dto;
    }

    public Donacion convertToEntity(DonacionDTO dto,
                                    Usuario usuario,
                                    EntidadReceptora entidad,
                                    Campania campania) {
        Donacion donacion = new Donacion();
        donacion.setUsuario(usuario);
        donacion.setEntidadReceptora(entidad);
        donacion.setCampania(campania);
        donacion.setMonto(dto.getMonto());
        donacion.setFecha(dto.getFecha());
        donacion.setMetodoPago(dto.getMetodoPago());

        try {
            String datosPagoJson = objectMapper.writeValueAsString(dto.getDatosPago());
            donacion.setDatosPago(datosPagoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir datosPago a JSON", e);
        }

        return donacion;
    }
}
