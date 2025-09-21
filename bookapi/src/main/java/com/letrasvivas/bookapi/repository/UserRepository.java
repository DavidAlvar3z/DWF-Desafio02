package com.letrasvivas.bookapi.repository;

import com.letrasvivas.bookapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email (unique constraint)
    Optional<User> findByEmail(String email);

    // Check if email exists (useful for validation)
    boolean existsByEmail(String email);

    // Find active users
    List<User> findByIsActiveTrue();

    // Find inactive users
    List<User> findByIsActiveFalse();

    // Search users by first name or last name (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find users by age range
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    // Find users with active subscriptions
    @Query("SELECT DISTINCT u FROM User u JOIN u.subscriptions s WHERE s.status = 'ACTIVE' AND u.isActive = true")
    List<User> findUsersWithActiveSubscriptions();

    // Find users without any subscriptions
    @Query("SELECT u FROM User u WHERE u.subscriptions IS EMPTY")
    List<User> findUsersWithoutSubscriptions();

    // Count active users
    long countByIsActiveTrue();

    // Find users by phone number pattern
    List<User> findByPhoneNumberContaining(String phonePattern);

    // Advanced search with multiple criteria
    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "(:minAge IS NULL OR u.age >= :minAge) AND " +
            "(:maxAge IS NULL OR u.age <= :maxAge)")
    Page<User> findUsersWithCriteria(@Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("email") String email,
                                     @Param("isActive") Boolean isActive,
                                     @Param("minAge") Integer minAge,
                                     @Param("maxAge") Integer maxAge,
                                     Pageable pageable);

    // Find users with subscription count
    @Query("SELECT u FROM User u LEFT JOIN u.subscriptions s GROUP BY u.id HAVING COUNT(s.id) >= :minSubscriptions")
    List<User> findUsersWithMinimumSubscriptions(@Param("minSubscriptions") long minSubscriptions);

    // Get user statistics
    @Query("SELECT COUNT(u) as totalUsers, " +
            "COUNT(CASE WHEN u.isActive = true THEN 1 END) as activeUsers, " +
            "COUNT(CASE WHEN u.isActive = false THEN 1 END) as inactiveUsers, " +
            "AVG(u.age) as averageAge " +
            "FROM User u")
    Object[] getUserStatistics();
}