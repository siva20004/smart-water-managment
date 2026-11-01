package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.DatabaseHealthInfo;
import com.veloia.smartwater.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class HealthService {

    private final DataSource dataSource;
    private final String environment;
    private final String appVersion;
    private final long startTimeMillis;

    public HealthService(
            @Autowired(required = false) DataSource dataSource,
            @Value("${spring.profiles.active:default}") String environment,
            @Value("${app.version:1.0.0}") String appVersion) {
        this.dataSource = dataSource;
        this.environment = environment;
        this.appVersion = appVersion;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public HealthResponse getSystemHealth() {
        DatabaseHealthInfo dbHealth = checkDatabaseHealth();

        String overallStatus = "UP";
        if ("DOWN".equals(dbHealth.getStatus())) {
            overallStatus = "DEGRADED";
        }

        long uptimeSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;

        Map<String, Object> systemMetrics = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        systemMetrics.put("totalMemoryMb", runtime.totalMemory() / (1024 * 1024));
        systemMetrics.put("freeMemoryMb", runtime.freeMemory() / (1024 * 1024));
        systemMetrics.put("maxMemoryMb", runtime.maxMemory() / (1024 * 1024));
        systemMetrics.put("availableProcessors", runtime.availableProcessors());
        systemMetrics.put("jvmUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());

        return new HealthResponse(
                overallStatus,
                "Smart Water Management & Analytics Backend",
                appVersion,
                environment,
                Instant.now(),
                uptimeSeconds,
                dbHealth,
                systemMetrics
        );
    }

    private DatabaseHealthInfo checkDatabaseHealth() {
        if (dataSource == null) {
            return new DatabaseHealthInfo("NOT_CONFIGURED", "None", 0L, "No DataSource configured");
        }

        long start = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SELECT 1");
            long responseTime = System.currentTimeMillis() - start;

            DatabaseMetaData metaData = conn.getMetaData();
            String productName = metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();

            return new DatabaseHealthInfo("UP", productName, responseTime, "Database connection operational");
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - start;
            return new DatabaseHealthInfo("DOWN", "PostgreSQL", responseTime, "Connection failed: " + e.getMessage());
        }
    }
}
