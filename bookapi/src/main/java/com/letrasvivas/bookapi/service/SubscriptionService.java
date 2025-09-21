package com.letrasvivas.bookapi.service;

import com.letrasvivas.bookapi.dto.request.CreateSubscriptionRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateSubscriptionRequestDTO;
import com.letrasvivas.bookapi.dto.response.SubscriptionResponseDTO;
import com.letrasvivas.bookapi.entity.Subscription;
import com.letrasvivas.bookapi.entity.Subscription.SubscriptionStatus;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.exception.ResourceNotFoundException;
import com.letrasvivas.bookapi.exception.BusinessValidationException;
import com.letrasvivas.bookapi.repository.SubscriptionRepository;
import com.letrasvivas.bookapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all subscriptions with pagination
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponseDTO> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get subscription by ID
     */
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        return convertToResponseDTO(subscription);
    }

    /**
     * Create a new subscription
     */
    public SubscriptionResponseDTO createSubscription(CreateSubscriptionRequestDTO requestDTO) {
        // Validate user exists
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId()));

        // Check for duplicate active subscriptions of same plan
        List<Subscription> existingActiveSubscriptions = subscriptionRepository
                .findActiveSubscriptionsByUserAndPlan(requestDTO.getUserId(), requestDTO.getPlanName());

        if (!existingActiveSubscriptions.isEmpty()) {
            throw new BusinessValidationException("User already has an active subscription for plan: " + requestDTO.getPlanName());
        }

        Subscription subscription = convertToEntity(requestDTO, user);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }

    /**
     * Update an existing subscription
     */
    public SubscriptionResponseDTO updateSubscription(Long id, UpdateSubscriptionRequestDTO requestDTO) {
        Subscription existingSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        updateSubscriptionFromDTO(existingSubscription, requestDTO);
        Subscription updatedSubscription = subscriptionRepository.save(existingSubscription);
        return convertToResponseDTO(updatedSubscription);
    }

    /**
     * Delete subscription by ID
     */
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    /**
     * Cancel subscription (set status to CANCELLED)
     */
    public SubscriptionResponseDTO cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToResponseDTO(updatedSubscription);
    }

    /**
     * Get subscriptions by user ID
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscriptionsByUserId(Long userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active subscriptions by user ID
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getActiveSubscriptionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return subscriptionRepository.findActiveSubscriptionsByUserId(userId, LocalDate.now())
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get subscriptions by status
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscriptionsByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expiring subscriptions (within next N days)
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getExpiringSubscriptions(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);

        return subscriptionRepository.findExpiringSubscriptions(today, futureDate)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expired but still active subscriptions
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getExpiredButActiveSubscriptions() {
        return subscriptionRepository.findExpiredButActiveSubscriptions(LocalDate.now())
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update expired subscriptions to EXPIRED status
     */
    public int updateExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredButActiveSubscriptions(LocalDate.now());

        expiredSubscriptions.forEach(subscription -> subscription.setStatus(SubscriptionStatus.EXPIRED));
        subscriptionRepository.saveAll(expiredSubscriptions);

        return expiredSubscriptions.size();
    }

    /**
     * Search subscriptions by plan name
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> searchSubscriptionsByPlanName(String planName) {
        return subscriptionRepository.findByPlanNameContainingIgnoreCase(planName)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get subscriptions by price range
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscriptionsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return subscriptionRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Advanced search with multiple criteria
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponseDTO> searchSubscriptions(String planName, SubscriptionStatus status,
                                                             BigDecimal minPrice, BigDecimal maxPrice,
                                                             LocalDate startDate, LocalDate endDate,
                                                             Long userId, Pageable pageable) {
        return subscriptionRepository.findSubscriptionsWithCriteria(planName, status, minPrice, maxPrice,
                        startDate, endDate, userId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get subscription count by status
     */
    @Transactional(readOnly = true)
    public long getSubscriptionCountByStatus(SubscriptionStatus status) {
        return subscriptionRepository.countByStatus(status);
    }

    /**
     * Calculate revenue by date range
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = subscriptionRepository.calculateRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    /**
     * Get most popular plans
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostPopularPlans() {
        return subscriptionRepository.findMostPopularPlans();
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Convert CreateSubscriptionRequestDTO to Subscription entity
     */
    private Subscription convertToEntity(CreateSubscriptionRequestDTO requestDTO, User user) {
        Subscription subscription = new Subscription();
        subscription.setPlanName(requestDTO.getPlanName());
        subscription.setPrice(requestDTO.getPrice());
        subscription.setStartDate(requestDTO.getStartDate());
        subscription.setDurationMonths(requestDTO.getDurationMonths());
        subscription.setDescription(requestDTO.getDescription());
        subscription.setAutoRenewal(requestDTO.getAutoRenewal() != null ? requestDTO.getAutoRenewal() : false);
        subscription.setUser(user);
        return subscription;
    }

    /**
     * Update Subscription entity from UpdateSubscriptionRequestDTO
     */
    private void updateSubscriptionFromDTO(Subscription subscription, UpdateSubscriptionRequestDTO requestDTO) {
        if (requestDTO.getPlanName() != null) {
            subscription.setPlanName(requestDTO.getPlanName());
        }
        if (requestDTO.getPrice() != null) {
            subscription.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getStartDate() != null) {
            subscription.setStartDate(requestDTO.getStartDate());
        }
        if (requestDTO.getDurationMonths() != null) {
            subscription.setDurationMonths(requestDTO.getDurationMonths());
        }
        if (requestDTO.getDescription() != null) {
            subscription.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getAutoRenewal() != null) {
            subscription.setAutoRenewal(requestDTO.getAutoRenewal());
        }
        if (requestDTO.getStatus() != null) {
            subscription.setStatus(SubscriptionStatus.valueOf(requestDTO.getStatus()));
        }
    }

    /**
     * Convert Subscription entity to SubscriptionResponseDTO
     */
    private SubscriptionResponseDTO convertToResponseDTO(Subscription subscription) {
        SubscriptionResponseDTO responseDTO = new SubscriptionResponseDTO();
        responseDTO.setId(subscription.getId());
        responseDTO.setPlanName(subscription.getPlanName());
        responseDTO.setPrice(subscription.getPrice());
        responseDTO.setStartDate(subscription.getStartDate());
        responseDTO.setEndDate(subscription.getEndDate());
        responseDTO.setDurationMonths(subscription.getDurationMonths());
        responseDTO.setStatus(subscription.getStatus().name());
        responseDTO.setDescription(subscription.getDescription());
        responseDTO.setAutoRenewal(subscription.getAutoRenewal());
        responseDTO.setCreatedAt(subscription.getCreatedAt());
        responseDTO.setUpdatedAt(subscription.getUpdatedAt());

        // Set user information
        User user = subscription.getUser();
        if (user != null) {
            responseDTO.setUserId(user.getId());
            responseDTO.setUserFullName(user.getFullName());
            responseDTO.setUserEmail(user.getEmail());
        }

        // Set computed fields
        responseDTO.setIsExpired(subscription.isExpired());
        responseDTO.setIsActive(subscription.isActive());
        responseDTO.setDaysUntilExpiration(subscription.getDaysUntilExpiration());

        return responseDTO;
    }
}