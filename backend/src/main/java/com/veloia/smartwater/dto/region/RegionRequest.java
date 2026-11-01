package com.veloia.smartwater.dto.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegionRequest {

    @NotBlank(message = "Region code is required")
    @Size(min = 2, max = 32, message = "Region code must be between 2 and 32 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Region code must contain only uppercase alphanumeric characters, dashes, or underscores")
    private String code;

    @NotBlank(message = "Region name is required")
    @Size(max = 128, message = "Region name must not exceed 128 characters")
    private String name;

    @NotBlank(message = "Prefecture is required")
    @Size(max = 64, message = "Prefecture must not exceed 64 characters")
    private String prefecture;

    private String description;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|PLANNED)$", message = "Status must be ACTIVE, INACTIVE, or PLANNED")
    private String status = "ACTIVE";

    public RegionRequest() {
    }

    public RegionRequest(String code, String name, String prefecture, String description, String status) {
        this.code = code;
        this.name = name;
        this.prefecture = prefecture;
        this.description = description;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
