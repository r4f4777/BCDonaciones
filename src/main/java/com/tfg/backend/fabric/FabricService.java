/*package com.tfg.backend.fabric;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Properties;

@Service
public class FabricService {

    private final HFClient client;
    private final Channel  channel;
    private final ChaincodeID chaincodeID;

    public FabricService(
            FabricUser fabricUser,
            @Value("${fabric.peer.tlsCert}") Resource peerTlsCert,
            @Value("${fabric.orderer.tlsCert}") Resource ordererTlsCert
    ) throws Exception {
        // 1) Cliente y crypto
        this.client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(fabricUser);

        // 2) Construir canal
        this.channel = client.newChannel("mychannel");

        // 3) Peer
        Properties peerProps = new Properties();
        // en lugar de pemFile, cargamos el contenido del PEM en memoria
        byte[] peerPem = peerTlsCert.getInputStream().readAllBytes();
        peerProps.put("pemBytes", peerPem);
        peerProps.put("sslProvider", "openSSL");
        peerProps.put("negotiationType", "TLS");
        Peer peer = client.newPeer(
                "peer0.org1.example.com",
                "grpcs://localhost:7051",
                peerProps
        );
        this.channel.addPeer(peer);

        // 4) Orderer (si lo necesitas para invocar/escuchar eventos)
        Properties ordProps = new Properties();
        byte[] ordPem = ordererTlsCert.getInputStream().readAllBytes();
        ordProps.put("pemBytes", ordPem);
        ordProps.put("sslProvider", "openSSL");
        ordProps.put("negotiationType", "TLS");
        Orderer orderer = client.newOrderer(
                "orderer.example.com",
                "grpcs://orderer.example.com:7050",
                ordProps
        );
        this.channel.addOrderer(orderer);

        // 5) Inicializar canal
        this.channel.initialize();

        // 6) ChaincodeID
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
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        request.setChaincodeID(chaincodeID);
        request.setFcn("RegistrarDonacion");
        request.setArgs(id, donanteID, monto, campaniaID, entidadID);

        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
        for (ProposalResponse res : responses) {
            if (res.getStatus() != ProposalResponse.Status.SUCCESS) {
                throw new IllegalStateException(
                        "Proposal failed on peer " + res.getPeer().getName() + ": " + res.getMessage()
                );
            }
        }
        channel.sendTransaction(responses).get();
        return "Donación registrada correctamente en blockchain.";
    }

    public void testConexion() throws Exception {
        System.out.println("✔ Conectado al canal: " + channel.getName());
        channel.getPeers().forEach(p -> System.out.println("  - Peer: " + p.getName()));
    }
}
*/