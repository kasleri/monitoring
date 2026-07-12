package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorNameAlreadyExistsException;
import com.aldisued.iot.monitoring.mapper.SensorMapper;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorService {

  private final SensorRepository sensorRepository;
  private final SensorMapper sensorMapper;

  public Sensor saveSensor(SensorDto sensor) {
    try {
      return sensorRepository.save(sensorMapper.toEntity(sensor));
    } catch (DataIntegrityViolationException e) {
      throw new SensorNameAlreadyExistsException(sensor.name());
    }
  }
}
