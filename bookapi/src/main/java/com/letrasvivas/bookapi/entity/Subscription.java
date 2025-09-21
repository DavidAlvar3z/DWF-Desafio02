package com.letrasvivas.bookapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name is mandatory")
    @Size(min = 3, max = 50, message = "Plan name must be between 3 and 50 characters")
    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must not exceed 9999.99")
    @Digits(integer = 4, fraction = 2, message = "Price must have at most 4 digits and 2 decimals")
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Start date is mandatory")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "Duration in months is mandatory")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 60, message = "Duration cannot exceed 60 months")
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "auto_renewal", nullable = false)
    private Boolean autoRenewal = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Enum for subscription status
    public enum SubscriptionStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        EXPIRED,
        CANCELLED
    }

    // Default constructor
    public Subscription() {}

    // Constructor for essential fields
    public Subscription(String planName, BigDecimal price, LocalDate startDate,
                        Integer durationMonths, SubscriptionStatus status, User user) {
        this.planName = planName;
        this.price = price;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
        this.status = status;
        this.user = user;
        calculateEndDate();
    }

    // JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (autoRenewal == null) {
            autoRenewal = false;
        }
        calculateEndDate();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateEndDate();
    }

    // Business logic methods
    private void calculateEndDate() {
        if (startDate != null && durationMonths != null) {
            this.endDate = startDate.plusMonths(durationMonths);
        }
    }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && !isExpired();
    }

    public long getDaysUntilExpiration() {
        if (endDate == null) return -1;
        return LocalDate.now().datesUntil(endDate).count();
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
        calculateEndDate();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
        calculateEndDate();
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", planName='" + planName + '\'' +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationMonths=" + durationMonths +
                ", status=" + status +
                ", autoRenewal=" + autoRenewal +
                ", createdAt=" + createdAt +
                '}';
    }
}