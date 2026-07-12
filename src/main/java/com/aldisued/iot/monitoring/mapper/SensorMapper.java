package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SensorMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "alerts", ignore = true)
  @Mapping(target = "sensorReadings", ignore = true)
  Sensor toEntity(SensorDto sensorDto);
}
