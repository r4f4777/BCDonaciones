package com.tfg.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "donaciones")
public class Donacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "entidad_receptora_id")
    private EntidadReceptora entidadReceptora;  // Ahora va a una ONG o ayuntamiento

    @ManyToOne
    @JoinColumn(name = "campania_id")
    private Campania campania;

    private Double monto;
    private LocalDate fecha;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "datos_pago", columnDefinition = "TEXT")
    private String datosPago; // Guardamos los detalles como JSON string

    // Getters y Setters

    public String getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(String datosPago) {
        this.datosPago = datosPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario user) {
        this.usuario = user;
    }

    public EntidadReceptora getEntidadReceptora() {
        return entidadReceptora;
    }

    public void setEntidadReceptora(EntidadReceptora entidadReceptora) {
        this.entidadReceptora = entidadReceptora;
    }

    public Campania getCampania() {
        return campania;
    }

    public void setCampania(Campania campania) {
        this.campania = campania;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
