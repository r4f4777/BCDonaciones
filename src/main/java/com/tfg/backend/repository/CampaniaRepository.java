package com.tfg.backend.repository;

import com.tfg.backend.model.Campania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CampaniaRepository extends JpaRepository<Campania, Long> {
    List<Campania> findDistinctByEntidadReceptoraDonacionesRecibidasUsuarioEmail(String email);
    Page<Campania> findAll(Pageable pageable);
    List<Campania> findByEntidadReceptoraId(Long entidadId);
    List<Campania> findDistinctByDonacionesUsuarioEmail(String email);


}
