package com.tfg.backend.repository;

import com.tfg.backend.model.DistribucionFondos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistribucionFondosRepository extends JpaRepository<DistribucionFondos, Long> {
    List<DistribucionFondos> findByEntidadReceptoraId(Long entidadId);  // Para ver cómo una entidad distribuye fondos
}
