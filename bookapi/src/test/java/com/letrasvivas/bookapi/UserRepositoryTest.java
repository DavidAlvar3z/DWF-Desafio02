package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setFirstName("John");
        testUser1.setLastName("Doe");
        testUser1.setEmail("john.doe@example.com");
        testUser1.setPhoneNumber("+1234567890");
        testUser1.setAge(25);
        testUser1.setIsActive(true);

        testUser2 = new User();
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setPhoneNumber("+1987654321");
        testUser2.setAge(30);
        testUser2.setIsActive(false);

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // When
        boolean result = userRepository.existsByEmail("john.doe@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // When
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    void findByIsActiveTrue_ShouldReturnOnlyActiveUsers() {
        // When
        List<User> result = userRepository.findByIsActiveTrue();

        // Then
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void findByIsActiveFalse_ShouldReturnOnlyInactiveUsers() {
        // When
        List<User> result = userRepository.findByIsActiveFalse();

        // Then
        assertEquals(1, result.size());
        assertEquals("Jane", result.get(0).getFirstName());
        assertFalse(result.get(0).getIsActive());
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingUsers() {
        // When
        List<User> result = userRepository.findByNameContainingIgnoreCase("john");

        // Then
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void findByAgeBetween_ShouldReturnUsersInAgeRange() {
        // When
        List<User> result = userRepository.findByAgeBetween(20, 26);

        // Then
        assertEquals(1, result.size());
        assertEquals(25, result.get(0).getAge());
    }

    @Test
    void countByIsActiveTrue_ShouldReturnActiveUserCount() {
        // When
        long result = userRepository.countByIsActiveTrue();

        // Then
        assertEquals(1, result);
    }

    @Test
    void findUsersWithCriteria_WithMultipleFilters_ShouldReturnFilteredUsers() {
        // When
        Page<User> result = userRepository.findUsersWithCriteria(
                "John", null, null, true, 20, 30, PageRequest.of(0, 10)
        );

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }

    @Test
    void findUsersWithCriteria_WithNullFilters_ShouldReturnAllUsers() {
        // When
        Page<User> result = userRepository.findUsersWithCriteria(
                null, null, null, null, null, null, PageRequest.of(0, 10)
        );

        // Then
        assertEquals(2, result.getContent().size());
    }
}