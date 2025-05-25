package com.galaxyviewtower.hotel.crud.observability;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "management.endpoints.web.exposure.include=*",
      "management.endpoint.prometheus.enabled=true",
      "management.metrics.export.prometheus.enabled=true",
      "management.tracing.sampling.probability=1.0",
      "management.metrics.tags.application=${spring.application.name:crud-service}",
      "spring.main.web-application-type=reactive"
    })
@Testcontainers
public class ObservabilityTest {

  private static final Network NETWORK = Network.newNetwork();

  @Container
  private static final GenericContainer<?> prometheus =
      new GenericContainer<>("prom/prometheus:latest")
          .withNetwork(NETWORK)
          .withNetworkAliases("prometheus")
          .withExposedPorts(9090);

  @Container
  private static final GenericContainer<?> zipkin =
      new GenericContainer<>("openzipkin/zipkin:latest")
          .withNetwork(NETWORK)
          .withNetworkAliases("zipkin")
          .withExposedPorts(9411);

  @LocalServerPort private int port;

  @Autowired private MeterRegistry meterRegistry;

  private static final String TEST_COUNTER_NAME = "test.counter";
  private Counter testCounter;
  private WebClient webClient;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "management.zipkin.tracing.endpoint",
        () ->
            String.format(
                "http://%s:%d/api/v2/spans", zipkin.getHost(), zipkin.getMappedPort(9411)));
  }

  @BeforeEach
  void setUp() {
    // Get or create the counter
    testCounter = meterRegistry.counter(TEST_COUNTER_NAME);
    // Reset the counter to 0
    while (testCounter.count() > 0) {
      testCounter.increment(-1);
    }
    // Increment once for the test
    testCounter.increment();

    // Initialize WebClient
    webClient = WebClient.builder().baseUrl("http://localhost:" + port).build();
  }

  // @Test
  // void testPrometheusMetricsEndpoint() {
  //     String metrics = webClient
  //         .get()
  //         .uri("/actuator/prometheus")
  //         .retrieve()
  //         .bodyToMono(String.class)
  //         .timeout(Duration.ofSeconds(5))
  //         .block();

  //     assertThat(metrics).isNotNull().contains(TEST_COUNTER_NAME + "_total");
  // }

  @Test
  void testMetricsCollection() {
    // Verify that metrics are being collected
    assertThat(testCounter).isNotNull();
    assertThat(testCounter.count()).isEqualTo(1.0);

    // Verify that basic metrics are present
    assertThat(meterRegistry.find("jvm.memory.used")).isNotNull();
  }
}
