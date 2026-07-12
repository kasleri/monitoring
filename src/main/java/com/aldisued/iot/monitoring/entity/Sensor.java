package com.aldisued.iot.monitoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "sensors")
@Entity
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SensorType type;

  @JsonIgnore
  @OneToMany(mappedBy = "sensor")
  private List<SensorReading> sensorReadings = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "sensor")
  private List<Alert> alerts = new ArrayList<>();

  public Sensor() {}

  public Sensor(String name, SensorType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sensor sensor = (Sensor) o;
    return Objects.equals(id, sensor.id) && Objects.equals(name, sensor.name)
        && type == sensor.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, type);
  }
}
