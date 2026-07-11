package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensor-readings")
@RequiredArgsConstructor
public class SensorReadingController {

  private final SensorReadingService sensorReadingService;

  @PostMapping
  public SensorReading saveSensorReading(@RequestBody SensorReadingDto sensorReadingDto) {
    return sensorReadingService.saveSensorReading(sensorReadingDto);
  }
}
