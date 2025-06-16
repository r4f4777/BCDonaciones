package com.tfg.backend.dto;

import java.time.LocalDate;

public class DistribucionFondosDTO {
    private Long entidadReceptoraId;
    private String destinatario;
    private Double monto;
    private LocalDate fecha;

    public Long getEntidadReceptoraId() {
        return entidadReceptoraId;
    }

    public void setEntidadReceptoraId(Long entidadReceptoraId) {
        this.entidadReceptoraId = entidadReceptoraId;
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
