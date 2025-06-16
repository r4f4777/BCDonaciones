package com.tfg.backend.fabric;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Set;

@Component
public class FabricUser implements User {

    @Value("${fabric.cert.path}")
    private Resource certResource;

    @Value("${fabric.key.path}")
    private Resource keyResource;

    @Override
    public String getName() {
        return "org1admin";
    }

    @Override
    public Set<String> getRoles() {
        return Collections.emptySet();
    }

    @Override
    public String getAccount() {
        return "";
    }

    @Override
    public String getAffiliation() {
        return "admin";
    }

    @Override
    public Enrollment getEnrollment() {
        try (InputStream certStream = certResource.getInputStream();
             InputStream keyStream = keyResource.getInputStream()) {

            String certString = new String(certStream.readAllBytes(), StandardCharsets.UTF_8);
            PrivateKey privateKey = Util.readPrivateKeyFromBytes(keyStream.readAllBytes());

            return new Enrollment() {
                @Override
                public PrivateKey getKey() {
                    return privateKey;
                }

                @Override
                public String getCert() {
                    return certString;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Error cargando las credenciales", e);
        }
    }

    @Override
    public String getMspId() {
        return "Org1MSP";
    }
}
