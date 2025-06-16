package com.tfg.backend.repository;

import com.tfg.backend.model.Donacion;
import com.tfg.backend.model.EntidadReceptora;
import com.tfg.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByUsuario(Usuario usuario);
    List<Donacion> findByUsuarioEmail(String email);
    List<Donacion> findByEntidadReceptora(EntidadReceptora entidadReceptora); // <- ESTE
    List<Donacion> findByCampania_Id(Long campaniaId);
}
