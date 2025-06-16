package com.tfg.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campanias")
public class Campania {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double metaRecaudacion;

    @ManyToOne
    @JoinColumn(name = "entidad_receptora_id")
    private EntidadReceptora entidadReceptora;

    /** Relaciones **/

    // 1) Lista de donaciones (para sumar montos)
    @OneToMany(mappedBy = "campania", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donacion> donaciones = new ArrayList<>();

    // 2) Lista de solicitudes de inscripción
    @OneToMany(mappedBy = "campania", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinRequest> joinRequests = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "campania_entidad",
            joinColumns = @JoinColumn(name = "campania_id"),
            inverseJoinColumns = @JoinColumn(name = "entidad_id")
    )
    private List<EntidadReceptora> entidadesReceptoras = new ArrayList<>();

    /** Getters & Setters **/

    public List<EntidadReceptora> getEntidadesReceptoras() {
        return entidadesReceptoras;
    }

    public void setEntidadesReceptoras(List<EntidadReceptora> entidadesReceptoras) {
        this.entidadesReceptoras = entidadesReceptoras;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getMetaRecaudacion() { return metaRecaudacion; }
    public void setMetaRecaudacion(Double metaRecaudacion) { this.metaRecaudacion = metaRecaudacion; }

    public EntidadReceptora getEntidadReceptora() { return entidadReceptora; }
    public void setEntidadReceptora(EntidadReceptora entidadReceptora) { this.entidadReceptora = entidadReceptora; }

    public List<Donacion> getDonaciones() { return donaciones; }
    public void setDonaciones(List<Donacion> donaciones) { this.donaciones = donaciones; }

    public List<JoinRequest> getJoinRequests() { return joinRequests; }
    public void setJoinRequests(List<JoinRequest> joinRequests) { this.joinRequests = joinRequests; }

    /** Conveniencia bidireccional **/
    public void addJoinRequest(JoinRequest jr) {
        joinRequests.add(jr);
        jr.setCampania(this);
    }
}
