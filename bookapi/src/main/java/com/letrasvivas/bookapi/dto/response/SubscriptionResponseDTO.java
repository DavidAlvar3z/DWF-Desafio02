package com.letrasvivas.bookapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubscriptionResponseDTO {

    private Long id;
    private String planName;
    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationMonths;
    private String status;
    private String description;
    private Boolean autoRenewal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // User information (minimal)
    private Long userId;
    private String userFullName;
    private String userEmail;

    // Additional computed fields
    private Boolean isExpired;
    private Boolean isActive;
    private Long daysUntilExpiration;

    // Default constructor
    public SubscriptionResponseDTO() {}

    // Constructor
    public SubscriptionResponseDTO(Long id, String planName, BigDecimal price, LocalDate startDate,
                                   LocalDate endDate, Integer durationMonths, String status,
                                   String description, Boolean autoRenewal, LocalDateTime createdAt,
                                   LocalDateTime updatedAt, Long userId, String userFullName, String userEmail) {
        this.id = id;
        this.planName = planName;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationMonths = durationMonths;
        this.status = status;
        this.description = description;
        this.autoRenewal = autoRenewal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;

        // Calculate computed fields
        this.isExpired = endDate != null && endDate.isBefore(LocalDate.now());
        this.isActive = "ACTIVE".equals(status) && !this.isExpired;
        this.daysUntilExpiration = endDate != null ? LocalDate.now().datesUntil(endDate).count() : -1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        // Recalculate computed fields when end date changes
        this.isExpired = endDate != null && endDate.isBefore(LocalDate.now());
        this.daysUntilExpiration = endDate != null ? LocalDate.now().datesUntil(endDate).count() : -1;
        this.isActive = "ACTIVE".equals(status) && !this.isExpired;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        // Recalculate isActive when status changes
        this.isActive = "ACTIVE".equals(status) && (this.isExpired == null || !this.isExpired);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getDaysUntilExpiration() {
        return daysUntilExpiration;
    }

    public void setDaysUntilExpiration(Long daysUntilExpiration) {
        this.daysUntilExpiration = daysUntilExpiration;
    }
}