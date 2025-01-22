package com.vodafone.orderapi;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.client.impl.util.VersionUtil;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.engine.EngineFactory;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@ZeebeProcessTest
public class GrpcZeebeEngineTest {
private ZeebeClient zeebeClient;
private ZeebeTestEngine zeebeTestEngine;
private RecordStream recordStream;


    @BeforeEach
    void setupGrpcServer() {
        zeebeTestEngine = EngineFactory.create();
        zeebeTestEngine.start();
        zeebeClient = zeebeTestEngine.createClient();
    }

    @AfterEach
    void tearDown() {
        zeebeTestEngine.stop();
        zeebeClient.close();
    }

    @Test
    void shouldRequestTopology() {
        // given

        // when
        final Topology topology = zeebeClient.newTopologyRequest().send().join();

        // then
        assertThat(topology.getClusterSize()).isEqualTo(1);
        assertThat(topology.getReplicationFactor()).isEqualTo(1);
        assertThat(topology.getPartitionsCount()).isEqualTo(1);
        assertThat(topology.getGatewayVersion()).isEqualTo(VersionUtil.getVersion());

        assertThat(topology.getBrokers()).hasSize(1);
        final BrokerInfo broker = topology.getBrokers().get(0);
        assertThat(broker.getAddress()).isEqualTo(zeebeTestEngine.getGatewayAddress());
        assertThat(broker.getVersion()).isEqualTo(VersionUtil.getVersion());

        assertThat(broker.getPartitions()).hasSize(1);
        final PartitionInfo partition = broker.getPartitions().get(0);
        assertThat(partition.getHealth()).isEqualTo(PartitionBrokerHealth.HEALTHY);
        assertThat(partition.isLeader()).isTrue();
        assertThat(partition.getRole()).isEqualTo(PartitionBrokerRole.LEADER);
        assertThat(partition.getPartitionId()).isEqualTo(1);
    }


}
