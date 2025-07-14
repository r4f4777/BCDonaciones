package com.tfg.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime; // Para el timestamp
import java.time.format.DateTimeParseException;

public class DonacionResponseDTO {

    // Campos que vienen directamente del chaincode
    @JsonProperty("DonationID")
    private String id; // Mapea el DonationID del chaincode a 'id' para la salida

    @JsonProperty("DonorID")
    private String donanteID;

    @JsonProperty("Amount")
    private Double monto;

    @JsonProperty("Timestamp")
    private String timestampChaincode; // String original del timestamp del chaincode

    @JsonProperty("CampaignID")
    private String campaniaID;

    @JsonProperty("ReceiverID")
    private String entidadID;

    // Campos adicionales que puedes querer rellenar desde tu BD o lógica de negocio
    // Por ejemplo, si tienes los nombres de campaña/entidad en tu DB.
    private String entidadReceptoraNombre;
    private String campaniaNombre;
    private LocalDate fecha; // Para la fecha más legible, derivado de timestampChaincode
    private String metodoPago;


    // --- Getters y Setters para todos los campos ---

    // Getters/Setters para los campos mapeados del chaincode
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDonanteID() { return donanteID; }
    public void setDonanteID(String donanteID) { this.donanteID = donanteID; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    // Setter para el timestamp del chaincode con lógica de conversión a LocalDate
    public String getTimestampChaincode() { return timestampChaincode; }
    public void setTimestampChaincode(String timestampChaincode) {
        this.timestampChaincode = timestampChaincode;
        if (timestampChaincode != null && !timestampChaincode.isEmpty()) {
            try {
                OffsetDateTime odt = OffsetDateTime.parse(timestampChaincode);
                this.fecha = odt.toLocalDate();
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing timestamp from chaincode: " + timestampChaincode + " - " + e.getMessage());
                this.fecha = null;
            }
        }
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    public String getCampaniaID() { return campaniaID; }
    public void setCampaniaID(String campaniaID) { this.campaniaID = campaniaID; }

    public String getEntidadID() { return entidadID; }
    public void setEntidadID(String entidadID) { this.entidadID = entidadID; }

    // Getters/Setters para campos adicionales
    public String getEntidadReceptoraNombre() { return entidadReceptoraNombre; }
    public void setEntidadReceptoraNombre(String entidadReceptoraNombre) { this.entidadReceptoraNombre = entidadReceptoraNombre; }

    public String getCampaniaNombre() { return campaniaNombre; }
    public void setCampaniaNombre(String campaniaNombre) { this.campaniaNombre = campaniaNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}