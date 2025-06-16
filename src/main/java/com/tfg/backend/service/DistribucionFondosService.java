package com.tfg.backend.service;

import com.tfg.backend.dto.DistribucionFondosDTO;
import com.tfg.backend.model.DistribucionFondos;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.repository.DistribucionFondosRepository;
import com.tfg.backend.repository.EntidadReceptoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DistribucionFondosService {

    private final DistribucionFondosRepository distribucionFondosRepository;
    private final EntidadReceptoraRepository entidadReceptoraRepository;

    public DistribucionFondosService(DistribucionFondosRepository distribucionFondosRepository, EntidadReceptoraRepository entidadReceptoraRepository) {
        this.distribucionFondosRepository = distribucionFondosRepository;
        this.entidadReceptoraRepository = entidadReceptoraRepository;
    }

    public List<DistribucionFondos> getAllDistribuciones() {
        return distribucionFondosRepository.findAll();
    }

    public Optional<DistribucionFondos> getDistribucionById(Long id) {
        return distribucionFondosRepository.findById(id);
    }

    public List<DistribucionFondos> getDistribucionesByEntidadId(Long entidadId) {
        return distribucionFondosRepository.findByEntidadReceptoraId(entidadId);
    }

    public DistribucionFondos createDistribucion(DistribucionFondosDTO dto) {
        // 1. Buscar la entidad receptora
        EntidadReceptora entidad = entidadReceptoraRepository.findById(dto.getEntidadReceptoraId())
                .orElseThrow(() -> new RuntimeException("Entidad receptora no encontrada"));

        // 2. Crear la distribución
        DistribucionFondos distribucion = new DistribucionFondos();
        distribucion.setEntidadReceptora(entidad);
        distribucion.setDestinatario(dto.getDestinatario());
        distribucion.setMonto(dto.getMonto());
        // Si en el DTO no viene fecha, puedes usar LocalDate.now() para poner la actual
        distribucion.setFecha(dto.getFecha() != null ? dto.getFecha() : java.time.LocalDate.now());

        // 3. Guardar y devolver
        return distribucionFondosRepository.save(distribucion);
    }

    public void deleteDistribucion(Long id) {
        distribucionFondosRepository.deleteById(id);
    }
}
