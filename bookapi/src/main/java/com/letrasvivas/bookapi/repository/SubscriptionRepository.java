package com.letrasvivas.bookapi.repository;

import com.letrasvivas.bookapi.entity.Subscription;
import com.letrasvivas.bookapi.entity.Subscription.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Find by user ID
    List<Subscription> findByUserId(Long userId);

    // Find by status
    List<Subscription> findByStatus(SubscriptionStatus status);

    // Find active subscriptions
    List<Subscription> findByStatusAndEndDateAfter(SubscriptionStatus status, LocalDate date);

    // Find subscriptions by plan name
    List<Subscription> findByPlanNameContainingIgnoreCase(String planName);

    // Find subscriptions by price range
    List<Subscription> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find expiring subscriptions (within next N days)
    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :today AND :futureDate AND s.status = 'ACTIVE'")
    List<Subscription> findExpiringSubscriptions(@Param("today") LocalDate today,
                                                 @Param("futureDate") LocalDate futureDate);

    // Find expired subscriptions that are still marked as active
    @Query("SELECT s FROM Subscription s WHERE s.endDate < :today AND s.status = 'ACTIVE'")
    List<Subscription> findExpiredButActiveSubscriptions(@Param("today") LocalDate today);

    // Count subscriptions by status
    long countByStatus(SubscriptionStatus status);

    // Find subscriptions with auto-renewal enabled
    List<Subscription> findByAutoRenewalTrue();

    // Find user's active subscriptions
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.endDate > :today")
    List<Subscription> findActiveSubscriptionsByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

    // Get subscription revenue by date range
    @Query("SELECT SUM(s.price) FROM Subscription s WHERE s.startDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // Find most popular plans
    @Query("SELECT s.planName, COUNT(s) as subscriptionCount FROM Subscription s GROUP BY s.planName ORDER BY COUNT(s) DESC")
    List<Object[]> findMostPopularPlans();

    // Advanced search for subscriptions
    @Query("SELECT s FROM Subscription s WHERE " +
            "(:planName IS NULL OR LOWER(s.planName) LIKE LOWER(CONCAT('%', :planName, '%'))) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.price <= :maxPrice) AND " +
            "(:startDate IS NULL OR s.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR s.endDate <= :endDate) AND " +
            "(:userId IS NULL OR s.user.id = :userId)")
    Page<Subscription> findSubscriptionsWithCriteria(@Param("planName") String planName,
                                                     @Param("status") SubscriptionStatus status,
                                                     @Param("minPrice") BigDecimal minPrice,
                                                     @Param("maxPrice") BigDecimal maxPrice,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("userId") Long userId,
                                                     Pageable pageable);

    // Find subscriptions ending soon for specific user
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.endDate BETWEEN :today AND :warningDate AND s.status = 'ACTIVE'")
    List<Subscription> findUserSubscriptionsEndingSoon(@Param("userId") Long userId,
                                                       @Param("today") LocalDate today,
                                                       @Param("warningDate") LocalDate warningDate);

    // Get subscription statistics
    @Query("SELECT COUNT(s) as totalSubscriptions, " +
            "COUNT(CASE WHEN s.status = 'ACTIVE' THEN 1 END) as activeSubscriptions, " +
            "COUNT(CASE WHEN s.endDate < :today AND s.status = 'ACTIVE' THEN 1 END) as expiredActive, " +
            "AVG(s.price) as averagePrice, " +
            "SUM(s.price) as totalRevenue " +
            "FROM Subscription s")
    Object[] getSubscriptionStatistics(@Param("today") LocalDate today);

    // Find duplicate active subscriptions for same user and plan
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.planName = :planName AND s.status = 'ACTIVE'")
    List<Subscription> findActiveSubscriptionsByUserAndPlan(@Param("userId") Long userId,
                                                            @Param("planName") String planName);
}