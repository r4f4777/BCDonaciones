package com.tfg.backend.dto;

import java.time.LocalDate;

public class DonacionDTO {

    // Usados para CREAR donaciones
    private Long entidadReceptoraId;
    private Long campaniaId;

    // Usados para MOSTRAR donaciones
    private String entidadReceptoraNombre;
    private String campaniaNombre;

    private Double monto;
    private LocalDate fecha;
    private Long id;

    private String donanteID;
    private String campaniaID;
    private String entidadID;

    private String metodoPago;
    private String datosPago; // Guardaremos un JSON string aquí

    // Getters y Setters

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(String datosPago) {
        this.datosPago = datosPago;
    }

    public String getDonanteID() {
        return donanteID;
    }
    public void setDonanteID(String donanteID) {
        this.donanteID = donanteID;
    }

    public String getCampaniaID() {
        return campaniaID;
    }
    public void setCampaniaID(String campaniaID) {
        this.campaniaID = campaniaID;
    }

    public String getEntidadID() {
        return entidadID;
    }
    public void setEntidadID(String entidadID) {
        this.entidadID = entidadID;
    }

    public Long getEntidadReceptoraId() {
        return entidadReceptoraId;
    }
    public void setEntidadReceptoraId(Long entidadReceptoraId) {
        this.entidadReceptoraId = entidadReceptoraId;
    }

    public Long getCampaniaId() {
        return campaniaId;
    }
    public void setCampaniaId(Long campaniaId) {
        this.campaniaId = campaniaId;
    }

    public String getEntidadReceptoraNombre() {
        return entidadReceptoraNombre;
    }
    public void setEntidadReceptoraNombre(String entidadReceptoraNombre) {
        this.entidadReceptoraNombre = entidadReceptoraNombre;
    }

    public String getCampaniaNombre() {
        return campaniaNombre;
    }
    public void setCampaniaNombre(String campaniaNombre) {
        this.campaniaNombre = campaniaNombre;
    }

    // Getters modificados para devolver Double y Long en lugar de String:
    public Double getMonto() {
        return monto != null ? monto : 0.0;
    }
    public void setMonto(Double cantidad) {
        this.monto = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
