package com.aldisued.iot.monitoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SensorNameAlreadyExistsException extends RuntimeException {

  public SensorNameAlreadyExistsException(String name) {
    super("A sensor with name '" + name + "' already exists");
  }
}
