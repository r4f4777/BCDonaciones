package com.tfg.backend;

import io.grpc.ManagedChannelRegistry;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BcDonacionesApplication {

    static {
        ManagedChannelRegistry.getDefaultRegistry().register(new NettyChannelProvider());
    }
    public static void main(String[] args) {
        // TrustStore configurado vía JVM arg o montado en contenedor
        System.out.println("🔍 Versión de Java: " + System.getProperty("java.version"));

        SpringApplication.run(BcDonacionesApplication.class, args);
    }

}
