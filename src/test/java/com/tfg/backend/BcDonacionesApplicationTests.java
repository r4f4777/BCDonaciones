package com.tfg.backend;

import org.hyperledger.fabric.client.Contract;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.TestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
// Importamos nuestro TestConfig para registrar el bean mockeado
@Import(BcDonacionesApplicationTests.TestConfig.class)
class BcDonacionesApplicationTests {

    // Ahora sí inyectamos el bean desde TestConfig
    @Autowired
    private Contract donationContract;

    @Test
    void contextLoads() {
        // Ya estamos usando el campo, así desaparece la advertencia “assigned but never accessed”
        assertThat(donationContract).isNotNull();
    }

    /**
     * Esta clase le dice a Spring cómo crear (mockear) el Contract
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public Contract donationContract() {
            return Mockito.mock(Contract.class);
        }
    }
}
