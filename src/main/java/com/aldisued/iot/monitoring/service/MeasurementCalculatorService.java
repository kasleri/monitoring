package com.aldisued.iot.monitoring.service;


import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MeasurementCalculatorService {

  public List<Double> filterByAverageDeviation(List<Double> values, Double deviation) {
    if (deviation == null || deviation < 0.0 || deviation > 1.0) {
      throw new IllegalArgumentException(
          "Deviation must be within [0.0, 1.0], but was: " + deviation);
    }
    if (values == null || values.isEmpty()) {
      return List.of();
    }

    double average = values.stream()
        .mapToDouble(Double::doubleValue)
        .average()
        .orElseThrow();
    double allowedDelta = Math.abs(average) * deviation;
    double min = average - allowedDelta;
    double max = average + allowedDelta;

    return values.stream()
        .filter(value -> value >= min && value <= max)
        .toList();
  }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    if (windowSize <= 0) {
      throw new IllegalArgumentException("Window size must be positive, but was: " + windowSize);
    }
    if (data == null || windowSize > data.size()) {
      throw new IllegalArgumentException(
          "Window size (" + windowSize + ") must not exceed the number of values");
    }

    List<Double> movingAverages = new ArrayList<>(data.size() - windowSize + 1);
    double windowSum = 0.0;
    for (int i = 0; i < data.size(); i++) {
      windowSum += data.get(i);
      if (i >= windowSize) {
        windowSum -= data.get(i - windowSize);
      }
      if (i >= windowSize - 1) {
        movingAverages.add(windowSum / windowSize);
      }
    }
    return movingAverages;
  }

}
