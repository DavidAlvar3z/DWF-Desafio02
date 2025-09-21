package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.entity.Subscription;
import com.letrasvivas.bookapi.entity.Subscription.SubscriptionStatus;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private User testUser;
    private Subscription activeSubscription;
    private Subscription expiredSubscription;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhoneNumber("+1234567890");
        testUser.setAge(25);
        testUser.setIsActive(true);
        entityManager.persistAndFlush(testUser);

        activeSubscription = new Subscription();
        activeSubscription.setPlanName("Premium Plan");
        activeSubscription.setPrice(new BigDecimal("29.99"));
        activeSubscription.setStartDate(LocalDate.now().minusMonths(1));
        activeSubscription.setDurationMonths(12);
        activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
        activeSubscription.setUser(testUser);
        entityManager.persistAndFlush(activeSubscription);

        expiredSubscription = new Subscription();
        expiredSubscription.setPlanName("Basic Plan");
        expiredSubscription.setPrice(new BigDecimal("19.99"));
        expiredSubscription.setStartDate(LocalDate.now().minusMonths(13));
        expiredSubscription.setDurationMonths(12);
        expiredSubscription.setStatus(SubscriptionStatus.ACTIVE);
        expiredSubscription.setUser(testUser);
        entityManager.persistAndFlush(expiredSubscription);
    }

    @Test
    void findByUserId_ShouldReturnUserSubscriptions() {
        // When
        List<Subscription> result = subscriptionRepository.findByUserId(testUser.getId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(sub -> sub.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void findByStatus_ShouldReturnSubscriptionsWithSpecificStatus() {
        // When
        List<Subscription> result = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE));
    }

    @Test
    void findByPlanNameContainingIgnoreCase_ShouldReturnMatchingSubscriptions() {
        // When
        List<Subscription> result = subscriptionRepository.findByPlanNameContainingIgnoreCase("premium");

        // Then
        assertEquals(1, result.size());
        assertEquals("Premium Plan", result.get(0).getPlanName());
    }

    @Test
    void findByPriceBetween_ShouldReturnSubscriptionsInPriceRange() {
        // When
        List<Subscription> result = subscriptionRepository.findByPriceBetween(
                new BigDecimal("25.00"), new BigDecimal("35.00")
        );

        // Then
        assertEquals(1, result.size());
        assertEquals("Premium Plan", result.get(0).getPlanName());
    }

    @Test
    void findExpiredButActiveSubscriptions_ShouldReturnExpiredActiveSubscriptions() {
        // When
        List<Subscription> result = subscriptionRepository.findExpiredButActiveSubscriptions(LocalDate.now());

        // Then
        assertEquals(1, result.size());
        assertEquals("Basic Plan", result.get(0).getPlanName());
        assertEquals(SubscriptionStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void findExpiringSubscriptions_ShouldReturnSubscriptionsEndingSoon() {
        // Create a subscription ending in 15 days
        Subscription soonToExpire = new Subscription();
        soonToExpire.setPlanName("Expiring Plan");
        soonToExpire.setPrice(new BigDecimal("15.99"));
        soonToExpire.setStartDate(LocalDate.now().minusMonths(11).minusDays(15));
        soonToExpire.setDurationMonths(12);
        soonToExpire.setStatus(SubscriptionStatus.ACTIVE);
        soonToExpire.setUser(testUser);
        entityManager.persistAndFlush(soonToExpire);

        // When
        List<Subscription> result = subscriptionRepository.findExpiringSubscriptions(
                LocalDate.now(), LocalDate.now().plusDays(30)
        );

        // Then
        assertEquals(1, result.size());
        assertEquals("Expiring Plan", result.get(0).getPlanName());
    }

    @Test
    void countByStatus_ShouldReturnCorrectCount() {
        // When
        long result = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);

        // Then
        assertEquals(2, result);
    }

    @Test
    void findActiveSubscriptionsByUserAndPlan_ShouldReturnMatchingSubscriptions() {
        // When
        List<Subscription> result = subscriptionRepository.findActiveSubscriptionsByUserAndPlan(
                testUser.getId(), "Premium Plan"
        );

        // Then
        assertEquals(1, result.size());
        assertEquals("Premium Plan", result.get(0).getPlanName());
        assertEquals(SubscriptionStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void calculateRevenueByDateRange_ShouldReturnTotalRevenue() {
        // When
        BigDecimal result = subscriptionRepository.calculateRevenueByDateRange(
                LocalDate.now().minusMonths(2), LocalDate.now()
        );

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("29.99"), result);
    }

    @Test
    void findMostPopularPlans_ShouldReturnPlansOrderedByCount() {
        // When
        List<Object[]> result = subscriptionRepository.findMostPopularPlans();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        // Should be ordered by count descending
        assertTrue(result.get(0)[1] instanceof Long);
    }
}