package com.tfg.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "distribucion_fondos")
public class DistribucionFondos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entidad_receptora_id")
    private EntidadReceptora entidadReceptora;

    private String destinatario; // Nombre o ID de la persona que recibe ayuda
    private Double monto;
    private LocalDate fecha;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntidadReceptora getEntidadReceptora() {
        return entidadReceptora;
    }

    public void setEntidadReceptora(EntidadReceptora entidadReceptora) {
        this.entidadReceptora = entidadReceptora;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
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

