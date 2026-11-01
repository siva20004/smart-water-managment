package com.veloia.smartwater.dto.quality;

import java.math.BigDecimal;

public class WaterQualityStatsDto {
    private long totalSamples;
    private long potableCount;
    private long monitoringRequiredCount;
    private long nonPotableCount;
    private BigDecimal complianceRatePercent;
    private BigDecimal avgPh;
    private BigDecimal avgTurbidityNtu;
    private BigDecimal avgChlorineMgL;
    private String simulatedDisclaimer = "Educational Simulation Only: Parameters evaluated against simulated municipal water guidelines.";

    public WaterQualityStatsDto() {
    }

    public long getTotalSamples() {
        return totalSamples;
    }

    public void setTotalSamples(long totalSamples) {
        this.totalSamples = totalSamples;
    }

    public long getPotableCount() {
        return potableCount;
    }

    public void setPotableCount(long potableCount) {
        this.potableCount = potableCount;
    }

    public long getMonitoringRequiredCount() {
        return monitoringRequiredCount;
    }

    public void setMonitoringRequiredCount(long monitoringRequiredCount) {
        this.monitoringRequiredCount = monitoringRequiredCount;
    }

    public long getNonPotableCount() {
        return nonPotableCount;
    }

    public void setNonPotableCount(long nonPotableCount) {
        this.nonPotableCount = nonPotableCount;
    }

    public BigDecimal getComplianceRatePercent() {
        return complianceRatePercent;
    }

    public void setComplianceRatePercent(BigDecimal complianceRatePercent) {
        this.complianceRatePercent = complianceRatePercent;
    }

    public BigDecimal getAvgPh() {
        return avgPh;
    }

    public void setAvgPh(BigDecimal avgPh) {
        this.avgPh = avgPh;
    }

    public BigDecimal getAvgTurbidityNtu() {
        return avgTurbidityNtu;
    }

    public void setAvgTurbidityNtu(BigDecimal avgTurbidityNtu) {
        this.avgTurbidityNtu = avgTurbidityNtu;
    }

    public BigDecimal getAvgChlorineMgL() {
        return avgChlorineMgL;
    }

    public void setAvgChlorineMgL(BigDecimal avgChlorineMgL) {
        this.avgChlorineMgL = avgChlorineMgL;
    }

    public String getSimulatedDisclaimer() {
        return simulatedDisclaimer;
    }

    public void setSimulatedDisclaimer(String simulatedDisclaimer) {
        this.simulatedDisclaimer = simulatedDisclaimer;
    }
}
