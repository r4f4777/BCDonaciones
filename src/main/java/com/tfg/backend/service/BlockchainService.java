package com.tfg.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tfg.backend.dto.DonacionDTO; // Tu DTO de entrada para crear
import com.tfg.backend.dto.DonacionResponseDTO; // Tu DTO para la respuesta de consulta
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID; // Para generar UUIDs si es necesario
import java.util.stream.Collectors; // No se usa actualmente, puedes quitarla si no la necesitas


@Service
public class BlockchainService {

    private final Contract donationContract;
    private final ObjectMapper objectMapper; // Inyecta ObjectMapper

    // Constructor
    public BlockchainService(Contract donationContract, ObjectMapper objectMapper) {
        this.donationContract = donationContract;
        this.objectMapper = objectMapper;
    }

    /**
     * Crea una nueva donación invocando CreateDonation en el chaincode.
     * Recibe los argumentos ya preparados para la blockchain.
     *
     * @param blockchainDonationID El ID único de la donación para la blockchain (ej. UUID o ID de DB convertido a String).
     * @param donorID El ID del donante para la blockchain (ej. email del usuario).
     * @param amount El monto de la donación como String (ej. "50.0").
     * @param campaignID El ID de la campaña para la blockchain.
     * @param receiverID El ID de la entidad receptora para la blockchain.
     */
    public void createDonation(
            String blockchainDonationID,
            String donorID,
            String amount,
            String campaignID,
            String receiverID
    ) throws EndorseException, SubmitException, CommitStatusException, CommitException {

        // --- Bloque de depuración (los System.out.println) ---
        System.out.println("DEBUG BC -- INVOCANDO CreateDonation en Chaincode:");
        System.out.println("DEBUG BC -- Donacion ID para BC: " + blockchainDonationID);
        System.out.println("DEBUG BC -- Donante ID para BC: " + donorID);
        System.out.println("DEBUG BC -- Monto para BC: " + amount);
        System.out.println("DEBUG BC -- Campaña ID para BC: " + campaignID);
        System.out.println("DEBUG BC -- Entidad ID para BC: " + receiverID);
        // --- Fin Bloque de depuración ---

        // Invoca la transacción en el chaincode
        try {
            donationContract.submitTransaction(
                    "CreateDonation",
                    blockchainDonationID,
                    donorID,
                    amount,
                    campaignID,
                    receiverID
            );
            System.out.println("DEBUG BC -- Transacción CreateDonation enviada con éxito a Fabric.");

        } catch (EndorseException e) {
            System.err.println("Error de Endorsement al crear donación en blockchain: " + e.getMessage());
            throw e; // Re-lanza para que el DonacionService lo capture
        } catch (SubmitException e) {
            System.err.println("Error de Submit al crear donación en blockchain: " + e.getMessage());
            throw e; // Re-lanza
        } catch (CommitStatusException e) {
            System.err.println("Error de Commit Status al crear donación en blockchain: " + e.getMessage());
            throw e; // Re-lanza
        } catch (CommitException e) {
            System.err.println("Error de Commit al crear donación en blockchain: " + e.getMessage());
            throw e; // Re-lanza
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            System.err.println("Error inesperado al crear donación en blockchain: " + e.getMessage());
            throw new RuntimeException("Error inesperado al interactuar con blockchain", e);
        }
    }

    /**
     * Consulta todas las donaciones invocando QueryAllDonations en el chaincode.
     * Devuelve una lista de DonacionResponseDTO.
     */
    public List<DonacionResponseDTO> getAllDonations()
            throws EndorseException, SubmitException, CommitStatusException {
        try {
            // Invoca la transacción de lectura del chaincode
            byte[] result = donationContract.evaluateTransaction("QueryAllDonations");

            // Si el resultado es vacío o nulo, retorna una lista vacía
            if (result == null || result.length == 0) {
                return Collections.emptyList();
            }

            // Deserializa el JSON array de bytes directamente a una lista de DonacionResponseDTO
            // ObjectMapper usa las anotaciones @JsonProperty en DonacionResponseDTO
            List<DonacionResponseDTO> donations = objectMapper.readValue(
                    new String(result, StandardCharsets.UTF_8),
                    new TypeReference<List<DonacionResponseDTO>>() {}
            );

            return donations;

        } catch (Exception e) {
            System.err.println("Error querying all donations: " + e.getMessage());
            throw new RuntimeException("Failed to query all donations from blockchain", e);
        }
    }
}