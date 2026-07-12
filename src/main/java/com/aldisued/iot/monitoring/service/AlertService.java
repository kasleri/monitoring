package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.exception.AlertNotFoundException;
import com.aldisued.iot.monitoring.mapper.AlertMapper;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final AlertMapper alertMapper;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  public Alert saveAlert(AlertDto alertDto) {
    // TODO: Task 6
    return null;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    return alertRepository.findFirstBySensorIdOrderByTimestampDesc(sensorId)
        .map(alertMapper::toDto)
        .orElseThrow(() -> new AlertNotFoundException(sensorId));
  }
}
