package com.tfg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;

    @Column(nullable = true)
    private String password;

    /**
     * Mapeo a la tabla usuario_roles (usuario_id, role).
     * Hibernate cargará aquí todos los roles asignados.
     */
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Rol.class)
    @CollectionTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "role")              // nombre de la columna en usuario_roles
    @Enumerated(EnumType.STRING)
    private Set<Rol> roles = new HashSet<>();

    // Relación con donaciones (solo para donantes)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<Donacion> donaciones;

    // Relación 1:1 con EntidadReceptora (para ONG o AYUNTAMIENTO)
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private EntidadReceptora entidadReceptora;

    // ————— Getters y setters —————

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public List<Donacion> getDonaciones() {
        return donaciones;
    }

    public void setDonaciones(List<Donacion> donaciones) {
        this.donaciones = donaciones;
    }

    public EntidadReceptora getEntidadReceptora() {
        return entidadReceptora;
    }

    public void setEntidadReceptora(EntidadReceptora entidadReceptora) {
        this.entidadReceptora = entidadReceptora;
    }
}
