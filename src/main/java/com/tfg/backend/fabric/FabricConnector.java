package com.tfg.backend.fabric;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;

@Component
public class FabricConnector {

    private final HFClient hfClient;
    private final Channel channel;
    private final ChaincodeID chaincodeID;

    public FabricConnector(
            @Value("${fabric.connection.profile}") Resource networkConfigResource,
            FabricUser fabricUser
    ) throws Exception {
        // 1) Crear HFClient + CryptoSuite
        this.hfClient = HFClient.createNewInstance();
        this.hfClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        // 2) Contexto de usuario (credenciales)
        this.hfClient.setUserContext(fabricUser);

        // 3) Cargar perfil YAML (peers, orderers y TLS)
        File configFile = networkConfigResource.getFile();
        NetworkConfig networkConfig = NetworkConfig.fromYamlFile(configFile);

        // 4) Inicializar canal 'mychannel' con toda la configuración del YAML
        this.channel = hfClient.loadChannelFromConfig("mychannel", networkConfig);
        this.channel.initialize();

        // 5) Preparar ChaincodeID
        this.chaincodeID = ChaincodeID.newBuilder()
                .setName("donation")
                .setVersion("1.0")
                .build();
    }

    public String registrarDonacion(
            String id,
            String donanteID,
            String monto,
            String campaniaID,
            String entidadID
    ) throws Exception {
        TransactionProposalRequest request = hfClient.newTransactionProposalRequest();
        request.setChaincodeID(chaincodeID);
        request.setFcn("RegistrarDonacion");
        request.setArgs(id, donanteID, monto, campaniaID, entidadID);

        // Enviar propuesta a los peers definidos en el YAML
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
        for (ProposalResponse resp : responses) {
            if (resp.getStatus() != ProposalResponse.Status.SUCCESS) {
                throw new IllegalStateException("Proposal failed on peer "
                        + resp.getPeer().getName() + ": " + resp.getMessage());
            }
        }

        // Enviar la transacción al orderer y esperar confirmación
        channel.sendTransaction(responses).get();

        return "Donación registrada correctamente.";
    }

    // Aquí podrías añadir otros métodos de invocación o consulta
}
