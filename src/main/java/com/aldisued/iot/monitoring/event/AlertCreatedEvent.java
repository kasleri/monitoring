package com.aldisued.iot.monitoring.event;

import com.aldisued.iot.monitoring.dto.AlertDto;

public record AlertCreatedEvent(AlertDto alertDto) {
}
