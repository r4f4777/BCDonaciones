package com.tfg.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.grpc.ManagedChannel;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Gateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
@ActiveProfiles("test")
public class FabricSmokeTest {

    @Autowired private Gateway gateway;
    @Autowired private Network network;
    @Autowired private Contract donationContract;
    @Autowired private ManagedChannel channel;

    @Test
    void contextLoads() {
        assertThat(gateway).isNotNull();
        assertThat(network).isNotNull();
        assertThat(donationContract).isNotNull();
        assertThat(channel).isNotNull();
    }

    @Test
    void canQueryChaincode() throws Exception {
        byte[] result = donationContract.evaluateTransaction("QueryAllDonations");
        assertThat(new String(result)).contains("donation");
    }
}
