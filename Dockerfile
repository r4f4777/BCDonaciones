FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/tfg-backend-0.0.1-SNAPSHOT.jar ./app.jar

# connection-org1.yaml SI es necesario y lo mantenemos
COPY fabric/connection-org1.yaml /app/fabric/connection/connection-org1.yaml

# crypto-config/peerOrganizations TAMBIÉN es necesario
# Copia los directorios completos de crypto-config para Org1 y Org2
COPY fabric/crypto-config/org1.example.com /app/fabric/crypto-config/org1.example.com
COPY fabric/crypto-config/org2.example.com /app/fabric/crypto-config/org2.example.com

# --- INICIO DE CAMBIOS PARA LOS CERTIFICADOS ---

# Copia los certificados CA de identidad y TLS CA al contenedor
# Estos son los certificados raíz de las CAs que firman los demás certificados.
COPY fabric/crypto-config/org1.example.com/ca/ca.org1.example.com-cert.pem /tmp/ca.org1.example.com-cert.pem
COPY fabric/crypto-config/org1.example.com/tlsca/tlsca.org1.example.com-cert.pem /tmp/tlsca.org1.example.com-cert.pem
COPY fabric/crypto-config/org2.example.com/ca/ca.org2.example.com-cert.pem /tmp/ca.org2.example.com-cert.pem
COPY fabric/crypto-config/org2.example.com/tlsca/tlsca.org2.example.com-cert.pem /tmp/tlsca.org2.example.com-cert.pem

# Importa los certificados al trust store de Java (cacerts)
# 'changeit' es la contraseña por defecto. Si la has cambiado, usa la tuya.
RUN keytool -import -alias ca-org1-cert -file /tmp/ca.org1.example.com-cert.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    keytool -import -alias tlsca-org1-cert -file /tmp/tlsca.org1.example.com-cert.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    keytool -import -alias ca-org2-cert -file /tmp/ca.org2.example.com-cert.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    keytool -import -alias tlsca-org2-cert -file /tmp/tlsca.org2.example.com-cert.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

# *** ¡NUEVA LÍNEA PARA VERIFICAR LOS CERTIFICADOS IMPORTADOS! ***
# Esto imprimirá el contenido del cacerts durante la construcción de la imagen.
RUN keytool -list -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -v

# Limpia los archivos temporales de certificados para reducir el tamaño de la imagen
RUN rm /tmp/ca.org1.example.com-cert.pem /tmp/tlsca.org1.example.com-cert.pem \
           /tmp/ca.org2.example.com-cert.pem /tmp/tlsca.org2.example.com-cert.pem

# --- FIN DE CAMBIOS PARA LOS CERTIFICADOS ---

# Diagnóstico (mantener para verificar que la clase principal se encuentra)
RUN jar tf app.jar | grep BcDonacionesApplication || echo "❌ Class not found in jar"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
