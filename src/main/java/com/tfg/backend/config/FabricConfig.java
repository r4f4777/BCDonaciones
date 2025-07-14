/*package com.tfg.backend.config;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ChannelCredentials;
import io.grpc.TlsChannelCredentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Hash;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.Signer;

@Configuration
@Profile("!test")
public class FabricConfig {

    private static final String MSP_ID        = System.getenv("FABRIC_MSP_ID");
    private static final String CHANNEL       = System.getenv("FABRIC_CHANNEL");
    private static final String CHAINCODE     = System.getenv("FABRIC_CHAINCODE");
    private static final String PEER_ENDPOINT = System.getenv("FABRIC_PEER_ENDPOINT");
    private static final String OVERRIDE_AUTH = System.getenv("FABRIC_PEER_AUTH");

    // Montado en /app/fabric dentro del contenedor
    private static final Path BASE_PATH     = Paths.get(System.getenv("FABRIC_BASE_PATH"));
    private static final Path ORG_PATH      = BASE_PATH.resolve("organizations/peerOrganizations/org1.example.com");
    private static final Path CERT_DIR      = ORG_PATH.resolve("users/User1@org1.example.com/msp/signcerts");
    private static final Path KEY_DIR       = ORG_PATH.resolve("users/User1@org1.example.com/msp/keystore");
    private static final Path TLS_CERT_PATH = ORG_PATH.resolve("peers/peer0.org1.example.com/tls/ca.crt");

    private ManagedChannel channel;
    private Gateway gateway;

    @Bean
    public ManagedChannel fabricChannel() throws IOException {
        ChannelCredentials creds = TlsChannelCredentials.newBuilder()
                .trustManager(TLS_CERT_PATH.toFile())
                .build();
        channel = Grpc.newChannelBuilder(PEER_ENDPOINT, creds)
                .overrideAuthority(OVERRIDE_AUTH)
                .build();
        return channel;
    }

    @Bean
    public Identity fabricIdentity() throws IOException, CertificateException {
        Path certFile = Files.list(CERT_DIR)
                .findFirst()
                .orElseThrow(() -> new IOException("Certificado X.509 no encontrado en " + CERT_DIR));
        try (var reader = Files.newBufferedReader(certFile)) {
            return new X509Identity(MSP_ID, Identities.readX509Certificate(reader));
        }
    }

    @Bean
    public Signer fabricSigner() throws IOException, InvalidKeyException {
        Path keyFile = Files.list(KEY_DIR)
                .findFirst()
                .orElseThrow(() -> new IOException("Clave privada no encontrada en " + KEY_DIR));
        try (var reader = Files.newBufferedReader(keyFile)) {
            return Signers.newPrivateKeySigner(Identities.readPrivateKey(reader));
        }
    }

    @Bean
    public Gateway fabricGateway(
            Identity id,
            Signer signer,
            ManagedChannel ch
    ) {
        gateway = Gateway.newInstance()
                .identity(id)
                .signer(signer)
                .hash(Hash.SHA256)
                .connection(ch)
                .evaluateOptions(opts -> opts.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(opts  -> opts.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(opts   -> opts.withDeadlineAfter(5, TimeUnit.SECONDS))
                .connect();
        return gateway;
    }

    @Bean
    public Network fabricNetwork(Gateway gw) {
        return gw.getNetwork(CHANNEL);
    }

    @Bean
    public Contract donationContract(Network net) {
        return net.getContract(CHAINCODE);
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (gateway != null)      gateway.close();
        if (channel != null)      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}

 */
// src/main/java/com/tfg/backend/config/FabricConfig.java
package com.tfg.backend.config;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials; // Sigue siendo necesario para crear credenciales TLS
import io.grpc.ChannelCredentials;

import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

@Configuration
@Profile("!test")
public class FabricConfig {

    private static final String MSP_ID        = System.getenv("FABRIC_MSP_ID");
    private static final String CHANNEL       = System.getenv("FABRIC_CHANNEL");
    private static final String CHAINCODE     = System.getenv("FABRIC_CHAINCODE");
    private static final String PEER_ENDPOINT = System.getenv("FABRIC_PEER_ENDPOINT");
    private static final String OVERRIDE_AUTH = System.getenv("FABRIC_PEER_AUTH");


    // Ruta base a crypto-config dentro del contenedor Docker.
    private static final Path ORG1_CRYPTO_BASE_PATH = Paths.get("/app/fabric/crypto-config/org1.example.com");

    // Rutas para el certificado y la clave privada del usuario (User1@org1.example.com)
    private static final Path USER_CERT_DIR = ORG1_CRYPTO_BASE_PATH.resolve("users/User1@org1.example.com/msp/signcerts");
    private static final Path USER_KEY_DIR  = ORG1_CRYPTO_BASE_PATH.resolve("users/User1@org1.example.com/msp/keystore");

    // NOTA: TLS_PEER_CA_PATH ya no se usa directamente en TlsChannelCredentials.newBuilder().trustManager()
    // porque los certificados se importan al cacerts de Java en el Dockerfile.
    // private static final Path TLS_PEER_CA_PATH = ORG1_CRYPTO_BASE_PATH.resolve("peers/peer0.org1.example.com/tls/ca.crt");


    private ManagedChannel channel;
    private Gateway gateway;

    @Bean
    public ManagedChannel fabricChannel() throws IOException {
        // *** CAMBIO CLAVE AQUÍ ***
        // No se especifica trustManager. Java usará su almacén de confianza (cacerts) por defecto,
        // donde ya hemos importado los certificados de la TLS CA en el Dockerfile.
        ChannelCredentials creds = TlsChannelCredentials.newBuilder().build();

        channel = Grpc.newChannelBuilder(PEER_ENDPOINT, creds)
                .overrideAuthority(OVERRIDE_AUTH)
                .build();
        return channel;
    }


    @Bean
    public Identity fabricIdentity() throws IOException, CertificateException {
        // Lee el certificado de usuario desde USER_CERT_DIR
        Path certFile = Files.list(USER_CERT_DIR).findFirst().orElseThrow(
                () -> new IllegalStateException("Certificado de usuario no encontrado en: " + USER_CERT_DIR + ". Asegúrate de que User1@org1.example.com exista y sus certs estén copiados."));
        return new X509Identity(
                MSP_ID,
                Identities.readX509Certificate(Files.newBufferedReader(certFile))
        );
    }

    @Bean
    public Signer fabricSigner() throws IOException, InvalidKeyException {
        // Lee la clave privada de usuario desde USER_KEY_DIR
        Path keyFile = Files.list(USER_KEY_DIR).findFirst().orElseThrow(
                () -> new IllegalStateException("Clave privada de usuario no encontrada en: " + USER_KEY_DIR + ". Asegúrate de que User1@org1.example.com exista y sus keys estén copiadas."));
        return Signers.newPrivateKeySigner(
                Identities.readPrivateKey(Files.newBufferedReader(keyFile))
        );
    }

    @Bean(destroyMethod = "close")
    public Gateway fabricGateway(Identity identity, Signer signer, ManagedChannel ch) {
        gateway = Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .hash(Hash.SHA256)
                .connection(ch)
                .evaluateOptions(o -> o.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(o  -> o.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(o   -> o.withDeadlineAfter(5, TimeUnit.SECONDS))
                .connect();
        return gateway;
    }

    @Bean
    public Network fabricNetwork(Gateway gw) {
        return gw.getNetwork(CHANNEL);
    }

    @Bean
    public Contract donationContract(Network net) {
        return net.getContract(CHAINCODE);
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (gateway  != null) gateway.close();
        if (channel  != null) channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}



/*
package com.tfg.backend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;

import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Hash;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;

@Configuration
//@Profile("!test")    // solo en perfiles distintos de "test"
public class FabricConfig {

    @Value("${fabric.msp-id}")
    private String mspId;

    @Value("${fabric.channel}")
    private String channelName;

    @Value("${fabric.chaincode}")
    private String chaincodeName;

    @Value("${fabric.peer.endpoint}")
    private String peerEndpoint;

    @Value("${fabric.peer.override-authority}")
    private String overrideAuth;

    @Value("${fabric.peer.tlsCert}")
    private Resource tlsCert;

    @Value("${fabric.wallet.path}")
    private Path walletPath;

    @Value("${fabric.user}")
    private String userId;

    private ManagedChannel channel;
    private Gateway gateway;

    @Bean
    public ManagedChannel fabricChannel() throws IOException {
        ChannelCredentials creds = TlsChannelCredentials.newBuilder()
                .trustManager(tlsCert.getFile())
                .build();

        channel = Grpc.newChannelBuilder(peerEndpoint, creds)
                .overrideAuthority(overrideAuth)
                .build();
        return channel;
    }

    @Bean
    public Identity fabricIdentity() throws IOException, CertificateException {
        Path certPem = walletPath.resolve(userId + "-cert.pem");
        return new X509Identity(
                mspId,
                Identities.readX509Certificate(Files.newBufferedReader(certPem))
        );
    }

    @Bean
    public Signer fabricSigner() throws IOException, InvalidKeyException {
        Path keyPem = walletPath.resolve(userId + "-key.pem");
        return Signers.newPrivateKeySigner(
                Identities.readPrivateKey(Files.newBufferedReader(keyPem))
        );
    }

    @Bean(destroyMethod = "close")
    public Gateway fabricGateway(Identity identity, Signer signer, ManagedChannel ch) {
        gateway = Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .hash(Hash.SHA256)
                .connection(ch)
                .connect();
        return gateway;
    }

    @Bean
    public Network fabricNetwork(Gateway gw) {
        return gw.getNetwork(channelName);
    }

    @Bean
    public Contract donationContract(Network net) {
        return net.getContract(chaincodeName);
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (gateway != null) gateway.close();
        if (channel != null) channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}*/
