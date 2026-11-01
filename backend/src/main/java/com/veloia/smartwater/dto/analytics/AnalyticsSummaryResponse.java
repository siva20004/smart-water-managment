package com.veloia.smartwater.dto.analytics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsSummaryResponse {
    // KPIs
    private long totalCustomers;
    private long totalActiveMeters;
    private BigDecimal totalConsumptionM3;
    private BigDecimal totalBilledRevenueJpy;
    private BigDecimal outstandingBalanceJpy;
    private long unpaidBillsCount;
    private long openLeakAlertsCount;
    private long criticalAlertsCount;
    private long pendingMaintenanceCount;
    private long totalQualitySamples;
    private double waterQualityPotabilityPercent;

    // Charts data
    private List<Map<String, Object>> monthlyConsumption;
    private List<Map<String, Object>> dailyConsumption;
    private List<Map<String, Object>> consumptionByRegion;
    private List<Map<String, Object>> consumptionByCustomerType;
    private List<Map<String, Object>> revenueOverTime;
    private List<Map<String, Object>> billStatusDistribution;
    private List<Map<String, Object>> alertsBySeverity;
    private List<Map<String, Object>> maintenanceStatusBreakdown;
    private List<Map<String, Object>> waterQualityTrends;

    public AnalyticsSummaryResponse() {
    }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalActiveMeters() { return totalActiveMeters; }
    public void setTotalActiveMeters(long totalActiveMeters) { this.totalActiveMeters = totalActiveMeters; }

    public BigDecimal getTotalConsumptionM3() { return totalConsumptionM3; }
    public void setTotalConsumptionM3(BigDecimal totalConsumptionM3) { this.totalConsumptionM3 = totalConsumptionM3; }

    public BigDecimal getTotalBilledRevenueJpy() { return totalBilledRevenueJpy; }
    public void setTotalBilledRevenueJpy(BigDecimal totalBilledRevenueJpy) { this.totalBilledRevenueJpy = totalBilledRevenueJpy; }

    public BigDecimal getOutstandingBalanceJpy() { return outstandingBalanceJpy; }
    public void setOutstandingBalanceJpy(BigDecimal outstandingBalanceJpy) { this.outstandingBalanceJpy = outstandingBalanceJpy; }

    public long getUnpaidBillsCount() { return unpaidBillsCount; }
    public void setUnpaidBillsCount(long unpaidBillsCount) { this.unpaidBillsCount = unpaidBillsCount; }

    public long getOpenLeakAlertsCount() { return openLeakAlertsCount; }
    public void setOpenLeakAlertsCount(long openLeakAlertsCount) { this.openLeakAlertsCount = openLeakAlertsCount; }

    public long getCriticalAlertsCount() { return criticalAlertsCount; }
    public void setCriticalAlertsCount(long criticalAlertsCount) { this.criticalAlertsCount = criticalAlertsCount; }

    public long getPendingMaintenanceCount() { return pendingMaintenanceCount; }
    public void setPendingMaintenanceCount(long pendingMaintenanceCount) { this.pendingMaintenanceCount = pendingMaintenanceCount; }

    public long getTotalQualitySamples() { return totalQualitySamples; }
    public void setTotalQualitySamples(long totalQualitySamples) { this.totalQualitySamples = totalQualitySamples; }

    public double getWaterQualityPotabilityPercent() { return waterQualityPotabilityPercent; }
    public void setWaterQualityPotabilityPercent(double waterQualityPotabilityPercent) { this.waterQualityPotabilityPercent = waterQualityPotabilityPercent; }

    public List<Map<String, Object>> getMonthlyConsumption() { return monthlyConsumption; }
    public void setMonthlyConsumption(List<Map<String, Object>> monthlyConsumption) { this.monthlyConsumption = monthlyConsumption; }

    public List<Map<String, Object>> getDailyConsumption() { return dailyConsumption; }
    public void setDailyConsumption(List<Map<String, Object>> dailyConsumption) { this.dailyConsumption = dailyConsumption; }

    public List<Map<String, Object>> getConsumptionByRegion() { return consumptionByRegion; }
    public void setConsumptionByRegion(List<Map<String, Object>> consumptionByRegion) { this.consumptionByRegion = consumptionByRegion; }

    public List<Map<String, Object>> getConsumptionByCustomerType() { return consumptionByCustomerType; }
    public void setConsumptionByCustomerType(List<Map<String, Object>> consumptionByCustomerType) { this.consumptionByCustomerType = consumptionByCustomerType; }

    public List<Map<String, Object>> getRevenueOverTime() { return revenueOverTime; }
    public void setRevenueOverTime(List<Map<String, Object>> revenueOverTime) { this.revenueOverTime = revenueOverTime; }

    public List<Map<String, Object>> getBillStatusDistribution() { return billStatusDistribution; }
    public void setBillStatusDistribution(List<Map<String, Object>> billStatusDistribution) { this.billStatusDistribution = billStatusDistribution; }

    public List<Map<String, Object>> getAlertsBySeverity() { return alertsBySeverity; }
    public void setAlertsBySeverity(List<Map<String, Object>> alertsBySeverity) { this.alertsBySeverity = alertsBySeverity; }

    public List<Map<String, Object>> getMaintenanceStatusBreakdown() { return maintenanceStatusBreakdown; }
    public void setMaintenanceStatusBreakdown(List<Map<String, Object>> maintenanceStatusBreakdown) { this.maintenanceStatusBreakdown = maintenanceStatusBreakdown; }

    public List<Map<String, Object>> getWaterQualityTrends() { return waterQualityTrends; }
    public void setWaterQualityTrends(List<Map<String, Object>> waterQualityTrends) { this.waterQualityTrends = waterQualityTrends; }
}
