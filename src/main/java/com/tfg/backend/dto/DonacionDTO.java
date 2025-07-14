package com.tfg.backend.dto;

// Quita esta importación si no la usas para otras cosas en este DTO
// import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
// Quita estas si no son relevantes para el DTO de entrada
// import java.time.OffsetDateTime;
// import java.time.format.DateTimeParseException;

public class DonacionDTO {

    // --- Campos usados para CREAR donaciones (provienen del frontend) ---
    private Long entidadReceptoraId;
    private Long campaniaId;
    private String metodoPago;
    private String datosPago; // Guardaremos un JSON string aquí

    private String fecha;

    // **¡IMPORTANTE! Estos campos NO DEBEN tener @JsonProperty si son para la entrada.**
    // Sus nombres deben coincidir con lo que el frontend envía (sin @JsonProperty).
    private String id; // Si el frontend enviara un 'id' de string, aunque para crear usamos UUID/DB ID
    private String donanteID; // Frontend envía este como el String ID para blockchain
    private Double monto; // Frontend envía este
    // private String timestampChaincode; // Este no es relevante para el DTO de entrada
    private String campaniaID; // Frontend envía este como el String ID para blockchain
    private String entidadID; // Frontend envía este como el String ID para blockchain

    // --- Campos adicionales para MOSTRAR donaciones (no son parte de la entrada directa) ---
    // Estos no deberían estar en el DTO de entrada, pero si los tienes ahí por alguna razón
    // no afectarán la entrada directamente, solo que tu DTO de entrada es "más grande" de lo necesario.
    // Lo ideal es tener un DTO de entrada más pequeño y enfocado.
    private String entidadReceptoraNombre;
    private String campaniaNombre;

    // --- Getters y Setters ---
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getFecha() {
        return fecha;
    }

    public Long getEntidadReceptoraId() { return entidadReceptoraId; }
    public void setEntidadReceptoraId(Long entidadReceptoraId) { this.entidadReceptoraId = entidadReceptoraId; }

    public Long getCampaniaId() { return campaniaId; }
    public void setCampaniaId(Long campaniaId) { this.campaniaId = campaniaId; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getDatosPago() { return datosPago; }
    public void setDatosPago(String datosPago) { this.datosPago = datosPago; }

    // Getters/Setters para los String IDs de blockchain y Monto (sin @JsonProperty)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDonanteID() { return donanteID; }
    public void setDonanteID(String donanteID) { this.donanteID = donanteID; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getCampaniaID() { return campaniaID; }
    public void setCampaniaID(String campaniaID) { this.campaniaID = campaniaID; }

    public String getEntidadID() { return entidadID; }
    public void setEntidadID(String entidadID) { this.entidadID = entidadID; }

    // Getters/Setters para campos de visualización (no vienen del frontend en la creación)
    public String getEntidadReceptoraNombre() { return entidadReceptoraNombre; }
    public void setEntidadReceptoraNombre(String entidadReceptoraNombre) { this.entidadReceptoraNombre = entidadReceptoraNombre; }

    public String getCampaniaNombre() { return campaniaNombre; }
    public void setCampaniaNombre(String campaniaNombre) { this.campaniaNombre = campaniaNombre; }


    // Quita estos getters/setters si no los usas en el DTO de entrada
    // public String getTimestampChaincode() { return timestampChaincode; }
    // public void setTimestampChaincode(String timestampChaincode) { /* ... */ }
}