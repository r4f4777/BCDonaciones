package com.tfg.backend.service;

import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.dto.EntidadReceptoraDTO;
import com.tfg.backend.repository.EntidadReceptoraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntidadReceptoraService {

    private final EntidadReceptoraRepository entidadReceptoraRepository;

    public EntidadReceptoraService(EntidadReceptoraRepository entidadReceptoraRepository) {
        this.entidadReceptoraRepository = entidadReceptoraRepository;
    }

    public List<EntidadReceptora> getAllEntidades() {
        return entidadReceptoraRepository.findAll();
    }

    public Optional<EntidadReceptora> getEntidadById(Long id) {
        return entidadReceptoraRepository.findById(id);
    }

    public EntidadReceptora createEntidad(EntidadReceptora entidadReceptora) {
        return entidadReceptoraRepository.save(entidadReceptora);
    }

    public List<EntidadReceptora> findByCampaniaId(Long campaniaId) {
        return entidadReceptoraRepository.findByCampanias_Id(campaniaId);
    }

    public void deleteEntidad(Long id) {
        entidadReceptoraRepository.deleteById(id);
    }

    public EntidadReceptoraDTO convertToDTO(EntidadReceptora entidad) {
        EntidadReceptoraDTO dto = new EntidadReceptoraDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setTipo(entidad.getTipo());
        return dto;
    }

    public Page<EntidadReceptora> getEntidadesPaginadas(Pageable pageable) {
        return entidadReceptoraRepository.findAll(pageable);
    }

}
