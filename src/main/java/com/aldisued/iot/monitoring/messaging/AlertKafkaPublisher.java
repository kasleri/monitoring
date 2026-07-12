package com.aldisued.iot.monitoring.messaging;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.event.AlertCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertKafkaPublisher {

  private static final String ALERTS_TOPIC = "alerts";

  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  @EventListener
  public void onAlertCreated(AlertCreatedEvent event) {
    kafkaTemplate.send(ALERTS_TOPIC, event.alertDto());
  }
}
