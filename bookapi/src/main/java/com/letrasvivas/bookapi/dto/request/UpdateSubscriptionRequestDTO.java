package com.letrasvivas.bookapi.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateSubscriptionRequestDTO {

    @Size(min = 3, max = 50, message = "Plan name must be between 3 and 50 characters")
    private String planName;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must not exceed 9999.99")
    @Digits(integer = 4, fraction = 2, message = "Price must have at most 4 digits and 2 decimals")
    private BigDecimal price;

    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 60, message = "Duration cannot exceed 60 months")
    private Integer durationMonths;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private Boolean autoRenewal;

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED|EXPIRED|CANCELLED",
            message = "Status must be one of: ACTIVE, INACTIVE, SUSPENDED, EXPIRED, CANCELLED")
    private String status;

    // Default constructor
    public UpdateSubscriptionRequestDTO() {}

    // Getters and Setters
    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}