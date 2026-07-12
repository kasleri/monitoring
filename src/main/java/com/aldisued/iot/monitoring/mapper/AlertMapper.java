package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertMapper {

  @Mapping(target = "sensorId", source = "sensor.id")
  AlertDto toDto(Alert alert);
}
