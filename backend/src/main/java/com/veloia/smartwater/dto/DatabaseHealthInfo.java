package com.veloia.smartwater.dto;

public class DatabaseHealthInfo {
    private String status;
    private String databaseProductName;
    private Long responseTimeMs;
    private String details;

    public DatabaseHealthInfo() {
    }

    public DatabaseHealthInfo(String status, String databaseProductName, Long responseTimeMs, String details) {
        this.status = status;
        this.databaseProductName = databaseProductName;
        this.responseTimeMs = responseTimeMs;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
