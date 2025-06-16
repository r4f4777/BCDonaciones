/*package com.tfg.backend.controller;
import com.tfg.backend.dto.DonacionDTO;
import com.tfg.backend.fabric.FabricService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FabricTestController {

    private final FabricService fabricService;

    // ✅ Spring inyectará automáticamente esta clase
    public FabricTestController(FabricService fabricService) {
        this.fabricService = fabricService;
    }

    @GetMapping("/fabric/test")
    public String testConexion() {
        try {
            fabricService.testConexion();
            return "✅ Conexión a Fabric exitosa.";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error conectando a Fabric: " + e.getMessage();
        }
    }

    @PostMapping("/fabric/donacion")
    public String registrarDonacion(@RequestBody DonacionDTO donacion) {
        try {
            fabricService.registrarDonacion(
                    String.valueOf(donacion.getId()),
                    donacion.getDonanteID(),
                    String.valueOf(donacion.getMonto()),
                    donacion.getCampaniaID(),
                    donacion.getEntidadID()
            );
            return "Donación registrada en Fabric correctamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al registrar donación en Fabric: " + e.getMessage();
        }
    }

}*/
