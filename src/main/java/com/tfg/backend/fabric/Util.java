package com.tfg.backend.fabric;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;

public class Util {

    public static PrivateKey readPrivateKeyFromBytes(byte[] data) throws Exception {
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data));
             PEMParser pemParser = new PEMParser(reader)) {

            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("No se pudo leer la clave privada correctamente.");
            }
        }
    }
}
