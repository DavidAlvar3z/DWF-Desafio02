package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.dto.request.CreateSubscriptionRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateSubscriptionRequestDTO;
import com.letrasvivas.bookapi.dto.response.SubscriptionResponseDTO;
import com.letrasvivas.bookapi.entity.Subscription;
import com.letrasvivas.bookapi.entity.Subscription.SubscriptionStatus;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.exception.BusinessValidationException;
import com.letrasvivas.bookapi.exception.ResourceNotFoundException;
import com.letrasvivas.bookapi.repository.SubscriptionRepository;
import com.letrasvivas.bookapi.repository.UserRepository;
import com.letrasvivas.bookapi.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private Subscription testSubscription;
    private CreateSubscriptionRequestDTO createSubscriptionRequestDTO;
    private UpdateSubscriptionRequestDTO updateSubscriptionRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testSubscription = new Subscription();
        testSubscription.setId(1L);
        testSubscription.setPlanName("Premium Plan");
        testSubscription.setPrice(new BigDecimal("29.99"));
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setDurationMonths(12);
        testSubscription.setStatus(SubscriptionStatus.ACTIVE);
        testSubscription.setUser(testUser);
        testSubscription.setCreatedAt(LocalDateTime.now());
        testSubscription.setUpdatedAt(LocalDateTime.now());

        createSubscriptionRequestDTO = new CreateSubscriptionRequestDTO();
        createSubscriptionRequestDTO.setPlanName("Basic Plan");
        createSubscriptionRequestDTO.setPrice(new BigDecimal("19.99"));
        createSubscriptionRequestDTO.setStartDate(LocalDate.now().plusDays(1));
        createSubscriptionRequestDTO.setDurationMonths(6);
        createSubscriptionRequestDTO.setUserId(1L);

        updateSubscriptionRequestDTO = new UpdateSubscriptionRequestDTO();
        updateSubscriptionRequestDTO.setPlanName("Updated Plan");
        updateSubscriptionRequestDTO.setPrice(new BigDecimal("39.99"));
    }

    @Test
    void getSubscriptionById_WithValidId_ShouldReturnSubscription() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

        // When
        SubscriptionResponseDTO result = subscriptionService.getSubscriptionById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Premium Plan", result.getPlanName());
        assertEquals(new BigDecimal("29.99"), result.getPrice());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    void getSubscriptionById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Given
        when(subscriptionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.getSubscriptionById(999L);
        });
        verify(subscriptionRepository).findById(999L);
    }

    @Test
    void createSubscription_WithValidData_ShouldCreateSubscription() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findActiveSubscriptionsByUserAndPlan(1L, "Basic Plan"))
                .thenReturn(Arrays.asList());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponseDTO result = subscriptionService.createSubscription(createSubscriptionRequestDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(subscriptionRepository).findActiveSubscriptionsByUserAndPlan(1L, "Basic Plan");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_WithInvalidUserId_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createSubscriptionRequestDTO.setUserId(999L);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.createSubscription(createSubscriptionRequestDTO);
        });
        verify(userRepository).findById(999L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void createSubscription_WithDuplicateActivePlan_ShouldThrowBusinessValidationException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findActiveSubscriptionsByUserAndPlan(1L, "Basic Plan"))
                .thenReturn(Arrays.asList(testSubscription));

        // When & Then
        assertThrows(BusinessValidationException.class, () -> {
            subscriptionService.createSubscription(createSubscriptionRequestDTO);
        });
        verify(userRepository).findById(1L);
        verify(subscriptionRepository).findActiveSubscriptionsByUserAndPlan(1L, "Basic Plan");
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void updateSubscription_WithValidData_ShouldUpdateSubscription() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponseDTO result = subscriptionService.updateSubscription(1L, updateSubscriptionRequestDTO);

        // Then
        assertNotNull(result);
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void cancelSubscription_WithValidId_ShouldCancelSubscription() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponseDTO result = subscriptionService.cancelSubscription(1L);

        // Then
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void getSubscriptionsByUserId_WithValidUserId_ShouldReturnSubscriptions() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Arrays.asList(testSubscription));

        // When
        List<SubscriptionResponseDTO> result = subscriptionService.getSubscriptionsByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Premium Plan", result.get(0).getPlanName());
        verify(userRepository).existsById(1L);
        verify(subscriptionRepository).findByUserId(1L);
    }

    @Test
    void getSubscriptionsByUserId_WithInvalidUserId_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.getSubscriptionsByUserId(999L);
        });
        verify(userRepository).existsById(999L);
        verify(subscriptionRepository, never()).findByUserId(999L);
    }

    @Test
    void getSubscriptionsByStatus_ShouldReturnFilteredSubscriptions() {
        // Given
        when(subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE))
                .thenReturn(Arrays.asList(testSubscription));

        // When
        List<SubscriptionResponseDTO> result = subscriptionService.getSubscriptionsByStatus(SubscriptionStatus.ACTIVE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        verify(subscriptionRepository).findByStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void getExpiringSubscriptions_ShouldReturnSubscriptionsEndingSoon() {
        // Given
        when(subscriptionRepository.findExpiringSubscriptions(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(testSubscription));

        // When
        List<SubscriptionResponseDTO> result = subscriptionService.getExpiringSubscriptions(30);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionRepository).findExpiringSubscriptions(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void updateExpiredSubscriptions_ShouldUpdateExpiredSubscriptionsToExpiredStatus() {
        // Given
        Subscription expiredSubscription = new Subscription();
        expiredSubscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findExpiredButActiveSubscriptions(any(LocalDate.class)))
                .thenReturn(Arrays.asList(expiredSubscription));
        when(subscriptionRepository.saveAll(anyList())).thenReturn(Arrays.asList(expiredSubscription));

        // When
        int result = subscriptionService.updateExpiredSubscriptions();

        // Then
        assertEquals(1, result);
        assertEquals(SubscriptionStatus.EXPIRED, expiredSubscription.getStatus());
        verify(subscriptionRepository).findExpiredButActiveSubscriptions(any(LocalDate.class));
        verify(subscriptionRepository).saveAll(anyList());
    }

    @Test
    void calculateRevenue_WithValidDateRange_ShouldReturnTotalRevenue() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedRevenue = new BigDecimal("299.99");
        when(subscriptionRepository.calculateRevenueByDateRange(startDate, endDate))
                .thenReturn(expectedRevenue);

        // When
        BigDecimal result = subscriptionService.calculateRevenue(startDate, endDate);

        // Then
        assertEquals(expectedRevenue, result);
        verify(subscriptionRepository).calculateRevenueByDateRange(startDate, endDate);
    }

    @Test
    void calculateRevenue_WithNullResult_ShouldReturnZero() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        when(subscriptionRepository.calculateRevenueByDateRange(startDate, endDate))
                .thenReturn(null);

        // When
        BigDecimal result = subscriptionService.calculateRevenue(startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(subscriptionRepository).calculateRevenueByDateRange(startDate, endDate);
    }
}