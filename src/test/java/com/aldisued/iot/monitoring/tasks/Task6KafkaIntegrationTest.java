package com.aldisued.iot.monitoring.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Sql(scripts = "/sql/task-5-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(properties = "app.kafka.reset-topics-on-startup=false")
public class Task6KafkaIntegrationTest extends IntegrationTestBase {

  private static final UUID SENSOR_ID = UUID.fromString("e3242ea2-0514-46d3-aad8-b2012980c41c");

  @TestConfiguration(proxyBeanMethods = false)
  static class KafkaTestcontainersConfiguration {

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
      return new KafkaContainer(DockerImageName.parse("apache/kafka:3.9.1"));
    }
  }

  @Autowired
  private KafkaContainer kafkaContainer;

  @Autowired
  private AlertRepository alertRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @AfterEach
  public void cleanup() {
    alertRepository.deleteAll();
  }

  @Test
  public void alertArrivingOnSensorAlertsTopicIsPersisted() throws Exception {
    var alertDto = new AlertDto(
        SENSOR_ID,
        "persisted-" + UUID.randomUUID(),
        LocalDateTime.parse("2026-07-13T10:15:30"));

    produceAsExternalSystem(alertDto);

    await().atMost(Duration.ofSeconds(30)).untilAsserted(
        () -> assertThat(alertRepository.count()).isEqualTo(1));

    Alert persisted = alertRepository.findAll().getFirst();
    assertThat(persisted.getMessage()).isEqualTo(alertDto.message());
    assertThat(persisted.getTimestamp()).isEqualTo(alertDto.timestamp());
    assertThat(persisted.getSensor().getId()).isEqualTo(SENSOR_ID);
  }

  @Test
  public void consumedAlertIsRepublishedOnAlertsTopic() throws Exception {
    var alertDto = new AlertDto(
        SENSOR_ID,
        "republished-" + UUID.randomUUID(),
        LocalDateTime.parse("2026-07-13T11:20:40"));

    produceAsExternalSystem(alertDto);

    ConsumerRecord<String, String> republished =
        pollAlertsTopicForMessageContaining(alertDto.message());

    assertThat(republished).as("republished alert on the 'alerts' topic").isNotNull();
    AlertDto publishedDto = objectMapper.readValue(republished.value(), AlertDto.class);
    assertThat(publishedDto).isEqualTo(alertDto);
  }

  @Test
  public void alertForUnknownSensorIsSkippedAndListenerKeepsConsuming() throws Exception {
    var unknownSensorDto = new AlertDto(
        UUID.randomUUID(),
        "unknown-sensor-" + UUID.randomUUID(),
        LocalDateTime.parse("2026-07-13T12:00:00"));
    var validDto = new AlertDto(
        SENSOR_ID,
        "after-failure-" + UUID.randomUUID(),
        LocalDateTime.parse("2026-07-13T12:00:05"));

    produceAsExternalSystem(unknownSensorDto, validDto);

    await().atMost(Duration.ofSeconds(60)).untilAsserted(
        () -> assertThat(alertRepository.count()).isEqualTo(1));

    Alert persisted = alertRepository.findAll().getFirst();
    assertThat(persisted.getMessage()).isEqualTo(validDto.message());
    assertThat(persisted.getSensor().getId()).isEqualTo(SENSOR_ID);
  }

  private void produceAsExternalSystem(AlertDto... alertDtos) throws Exception {
    try (var producer = new KafkaProducer<String, String>(Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()))) {
      for (AlertDto alertDto : alertDtos) {
        producer.send(new ProducerRecord<>(
            "sensor-alerts", objectMapper.writeValueAsString(alertDto))).get(15, TimeUnit.SECONDS);
      }
    }
  }

  private ConsumerRecord<String, String> pollAlertsTopicForMessageContaining(String marker) {
    try (var consumer = new KafkaConsumer<String, String>(Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
        ConsumerConfig.GROUP_ID_CONFIG, "test-" + UUID.randomUUID(),
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
      consumer.subscribe(List.of("alerts"));

      long deadline = System.currentTimeMillis() + 30_000;
      while (System.currentTimeMillis() < deadline) {
        for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(500))) {
          if (record.value().contains(marker)) {
            return record;
          }
        }
      }
      return null;
    }
  }
}
