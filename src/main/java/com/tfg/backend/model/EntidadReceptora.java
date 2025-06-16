package com.tfg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entidades_receptoras")
public class EntidadReceptora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo; // "ONG" o "AYUNTAMIENTO"

    @OneToMany(mappedBy = "entidadReceptora")
    @JsonIgnore
    private List<Donacion> donacionesRecibidas;

    @OneToMany(mappedBy = "entidadReceptora")
    @JsonIgnore
    private List<DistribucionFondos> distribuciones;

    // Nueva relación 1:1 con Usuario
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @ManyToMany(mappedBy = "entidadesReceptoras")
    private List<Campania> campanias = new ArrayList<>();






    // Getters y Setters
    public List<Campania> getCampanias() {
        return campanias;
    }

    public void setCampanias(List<Campania> campanias) {
        this.campanias = campanias;
    }


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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Donacion> getDonacionesRecibidas() {
        return donacionesRecibidas;
    }

    public void setDonacionesRecibidas(List<Donacion> donacionesRecibidas) {
        this.donacionesRecibidas = donacionesRecibidas;
    }

    public List<DistribucionFondos> getDistribuciones() {
        return distribuciones;
    }

    public void setDistribuciones(List<DistribucionFondos> distribuciones) {
        this.distribuciones = distribuciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
