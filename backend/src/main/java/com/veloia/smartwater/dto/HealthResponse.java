package com.veloia.smartwater.dto;

import java.time.Instant;
import java.util.Map;

public class HealthResponse {
    private String status;
    private String service;
    private String version;
    private String environment;
    private Instant timestamp;
    private long uptimeSeconds;
    private DatabaseHealthInfo database;
    private Map<String, Object> system;

    public HealthResponse() {
    }

    public HealthResponse(String status, String service, String version, String environment,
                          Instant timestamp, long uptimeSeconds, DatabaseHealthInfo database,
                          Map<String, Object> system) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.environment = environment;
        this.timestamp = timestamp;
        this.uptimeSeconds = uptimeSeconds;
        this.database = database;
        this.system = system;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public DatabaseHealthInfo getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseHealthInfo database) {
        this.database = database;
    }

    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }
}
