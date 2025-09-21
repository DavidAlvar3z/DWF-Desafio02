package com.letrasvivas.bookapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer age;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fullName;
    private Integer subscriptionCount;
    private List<UserSubscriptionSummaryDTO> activeSubscriptions;

    // Default constructor
    public UserResponseDTO() {}

    // Constructor
    public UserResponseDTO(Long id, String firstName, String lastName, String email,
                           String phoneNumber, Integer age, Boolean isActive,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fullName = firstName + " " + lastName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getSubscriptionCount() {
        return subscriptionCount;
    }

    public void setSubscriptionCount(Integer subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    public List<UserSubscriptionSummaryDTO> getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(List<UserSubscriptionSummaryDTO> activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    private void updateFullName() {
        if (firstName != null && lastName != null) {
            this.fullName = firstName + " " + lastName;
        }
    }

    // Inner class for subscription summary
    public static class UserSubscriptionSummaryDTO {
        private Long id;
        private String planName;
        private String status;
        private String endDate;

        public UserSubscriptionSummaryDTO() {}

        public UserSubscriptionSummaryDTO(Long id, String planName, String status, String endDate) {
            this.id = id;
            this.planName = planName;
            this.status = status;
            this.endDate = endDate;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}