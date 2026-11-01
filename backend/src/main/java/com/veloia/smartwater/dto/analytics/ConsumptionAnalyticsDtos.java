package com.veloia.smartwater.dto.analytics;

import java.math.BigDecimal;

public class ConsumptionAnalyticsDtos {

    public static class DailyConsumption {
        private String date;
        private BigDecimal consumptionM3;
        private long readingCount;

        public DailyConsumption() {}

        public DailyConsumption(String date, BigDecimal consumptionM3, long readingCount) {
            this.date = date;
            this.consumptionM3 = consumptionM3;
            this.readingCount = readingCount;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getConsumptionM3() { return consumptionM3; }
        public void setConsumptionM3(BigDecimal consumptionM3) { this.consumptionM3 = consumptionM3; }
        public long getReadingCount() { return readingCount; }
        public void setReadingCount(long readingCount) { this.readingCount = readingCount; }
    }

    public static class MonthlyConsumption {
        private String yearMonth;
        private BigDecimal consumptionM3;
        private long meterCount;

        public MonthlyConsumption() {}

        public MonthlyConsumption(String yearMonth, BigDecimal consumptionM3, long meterCount) {
            this.yearMonth = yearMonth;
            this.consumptionM3 = consumptionM3;
            this.meterCount = meterCount;
        }

        public String getYearMonth() { return yearMonth; }
        public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
        public BigDecimal getConsumptionM3() { return consumptionM3; }
        public void setConsumptionM3(BigDecimal consumptionM3) { this.consumptionM3 = consumptionM3; }
        public long getMeterCount() { return meterCount; }
        public void setMeterCount(long meterCount) { this.meterCount = meterCount; }
    }

    public static class CustomerConsumption {
        private Long customerId;
        private String customerNumber;
        private String customerName;
        private BigDecimal totalConsumptionM3;

        public CustomerConsumption() {}

        public CustomerConsumption(Long customerId, String customerNumber, String customerName, BigDecimal totalConsumptionM3) {
            this.customerId = customerId;
            this.customerNumber = customerNumber;
            this.customerName = customerName;
            this.totalConsumptionM3 = totalConsumptionM3;
        }

        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerNumber() { return customerNumber; }
        public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public BigDecimal getTotalConsumptionM3() { return totalConsumptionM3; }
        public void setTotalConsumptionM3(BigDecimal totalConsumptionM3) { this.totalConsumptionM3 = totalConsumptionM3; }
    }

    public static class RegionConsumption {
        private Long regionId;
        private String regionCode;
        private String regionName;
        private BigDecimal totalConsumptionM3;

        public RegionConsumption() {}

        public RegionConsumption(Long regionId, String regionCode, String regionName, BigDecimal totalConsumptionM3) {
            this.regionId = regionId;
            this.regionCode = regionCode;
            this.regionName = regionName;
            this.totalConsumptionM3 = totalConsumptionM3;
        }

        public Long getRegionId() { return regionId; }
        public void setRegionId(Long regionId) { this.regionId = regionId; }
        public String getRegionCode() { return regionCode; }
        public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
        public String getRegionName() { return regionName; }
        public void setRegionName(String regionName) { this.regionName = regionName; }
        public BigDecimal getTotalConsumptionM3() { return totalConsumptionM3; }
        public void setTotalConsumptionM3(BigDecimal totalConsumptionM3) { this.totalConsumptionM3 = totalConsumptionM3; }
    }
}
