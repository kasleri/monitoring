# IoT Monitoring System

We are building an **IoT monitoring system** to collect, process, and analyze device data.  
The project has already been started, and we are looking for it to be completed.

---

## 📋 Project Tasks

A detailed project description and the remaining tasks can be found in the [TASKS.md](./TASKS.md) file located in the root directory.

---

## 🚀 Assignment Instructions

1. Clone this repository
2. Review the [TASKS.md](./TASKS.md) file.
3. Complete the remaining implementation tasks.
4. Ensure the project is working end-to-end.
5. Upload the finished project into your own **GitHub repository**.
5. Share the repository link with us.

---

## ⏰ Timeline

Please complete the assignment **within one week**.

---

## 📂 Project Structure (expected)

```
monitoring/
└── src/
    ├── main/
    │   ├── java/com/aldisued/iot/monitoring/
    │   │   ├── controller/             # REST controllers (/sensors, /sensor-readings, /alerts)
    │   │   ├── service/                # business logic + measurement calculators
    │   │   ├── messaging/              # Kafka listeners + alert publisher
    │   │   ├── event/                  # application events (AlertCreatedEvent)
    │   │   ├── mapper/                 # MapStruct DTO ⇆ entity mappers
    │   │   ├── repository/             # Spring Data JPA repositories (+ JPQL queries)
    │   │   ├── entity/                 # JPA entities (Sensor, SensorReading, Alert)
    │   │   ├── dto/                    # request/response records
    │   │   └── exception/              # typed exceptions mapped to HTTP statuses
    │   └── resources/                  # datasource + Kafka configuration
    └── test/
        ├── java/com/aldisued/iot/monitoring/
        │   └── tasks/                  # Task0–Task10 tests + Kafka integration test
        └── resources/sql/              # per-task test fixtures
```

