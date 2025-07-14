package com.tfg.backend.controller;

import com.tfg.backend.dto.DonacionDTO; // Para crear donación
import com.tfg.backend.dto.DonacionResponseDTO; // Para listar donaciones
import com.tfg.backend.service.BlockchainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    /*@PostMapping("/donaciones")
    public ResponseEntity<Void> create(@RequestBody DonacionDTO donacionDTO) {
        try {
            blockchainService.createDonation(donacionDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating donation: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @GetMapping("/donaciones")
    public ResponseEntity<List<DonacionResponseDTO>> getAllDonations() {
        try {
            List<DonacionResponseDTO> donations = blockchainService.getAllDonations();
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting all donations: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}