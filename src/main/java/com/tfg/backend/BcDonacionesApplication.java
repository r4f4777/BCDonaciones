package com.tfg.backend;

import com.tfg.backend.config.SslBypassConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BcDonacionesApplication {

    public static void main(String[] args) {
        // 👇 Primero seteamos el trustStore
        System.setProperty("javax.net.ssl.trustStore", "/home/rafa/fabric-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        System.out.println("Usando trustStore: " + System.getProperty("javax.net.ssl.trustStore"));
        System.out.println("Password: " + System.getProperty("javax.net.ssl.trustStorePassword"));
        System.out.println("🔍 Versión de Java: " + System.getProperty("java.version"));


        // 👇 Después arrancamos la app
        SslBypassConfig.disableSslVerification(); // ⚠️ ignora validación SSL
        SpringApplication.run(BcDonacionesApplication.class, args);
    }

}
