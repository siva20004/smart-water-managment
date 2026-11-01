package com.veloia.smartwater.dto.location;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LocationRequest {

    @NotNull(message = "Region ID is required")
    private Long regionId;

    @NotBlank(message = "Location code is required")
    @Size(max = 64, message = "Location code must not exceed 64 characters")
    private String locationCode;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    private BigDecimal longitude;

    private BigDecimal elevationMeters;

    @NotBlank(message = "Installation type is required")
    @Pattern(regexp = "^(RESIDENTIAL|COMMERCIAL|INDUSTRIAL|PUBLIC_MUNICIPAL|TREATMENT_PLANT|PUMP_STATION)$",
             message = "Installation type must be RESIDENTIAL, COMMERCIAL, INDUSTRIAL, PUBLIC_MUNICIPAL, TREATMENT_PLANT, or PUMP_STATION")
    private String installationType;

    public LocationRequest() {
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getElevationMeters() {
        return elevationMeters;
    }

    public void setElevationMeters(BigDecimal elevationMeters) {
        this.elevationMeters = elevationMeters;
    }

    public String getInstallationType() {
        return installationType;
    }

    public void setInstallationType(String installationType) {
        this.installationType = installationType;
    }
}
