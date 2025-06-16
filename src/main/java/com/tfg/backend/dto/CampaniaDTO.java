package com.tfg.backend.dto;

public class CampaniaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double metaRecaudacion;
    private Long entidadReceptoraId;
    private int totalDonaciones;

    private double totalRecaudado;
    private double porcentajeAlcanzado;

    private String entidadReceptoraNombre;

    public String getEntidadReceptoraNombre() {
        return entidadReceptoraNombre;
    }

    public void setEntidadReceptoraNombre(String entidadReceptoraNombre) {
        this.entidadReceptoraNombre = entidadReceptoraNombre;
    }

    public double getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(double totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }

    public double getPorcentajeAlcanzado() {
        return porcentajeAlcanzado;
    }

    public void setPorcentajeAlcanzado(double porcentajeAlcanzado) {
        this.porcentajeAlcanzado = porcentajeAlcanzado;
    }

    public int getTotalDonaciones() {
        return totalDonaciones;
    }

    public void setTotalDonaciones(int totalDonaciones) {
        this.totalDonaciones = totalDonaciones;
    }

    public Long getEntidadReceptoraId() {
        return entidadReceptoraId;
    }

    public void setEntidadReceptoraId(Long entidadReceptoraId) {
        this.entidadReceptoraId = entidadReceptoraId;
    }

    public Double getMetaRecaudacion() {
        return metaRecaudacion;
    }

    public void setMetaRecaudacion(Double metaRecaudacion) {
        this.metaRecaudacion = metaRecaudacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
