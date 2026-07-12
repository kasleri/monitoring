package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

  @Query("""
      select avg(r.value)
      from SensorReading r
      where r.sensor.type = :type
        and r.timestamp between :from and :to
      """)
  Double findAverageValueBySensorTypeInPeriod(
      @Param("type") SensorType type,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  @Query("""
      select r.value
      from SensorReading r
      where r.sensor.type = :type
        and r.timestamp between :from and :to
      order by r.timestamp
      """)
  List<Double> findValuesBySensorTypeInPeriodOrderByTimestamp(
      @Param("type") SensorType type,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);
}
