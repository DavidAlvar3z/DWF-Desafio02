package com.letrasvivas.bookapi.service;

import com.letrasvivas.bookapi.dto.request.CreateUserRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateUserRequestDTO;
import com.letrasvivas.bookapi.dto.response.UserResponseDTO;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.exception.ResourceNotFoundException;
import com.letrasvivas.bookapi.exception.DuplicateResourceException;
import com.letrasvivas.bookapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponseDTO(user);
    }

    /**
     * Create a new user
     */
    public UserResponseDTO createUser(CreateUserRequestDTO requestDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("User with email " + requestDTO.getEmail() + " already exists");
        }

        User user = convertToEntity(requestDTO);
        User savedUser = userRepository.save(user);
        return convertToResponseDTO(savedUser);
    }

    /**
     * Update an existing user
     */
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO requestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DuplicateResourceException("User with email " + requestDTO.getEmail() + " already exists");
            }
        }

        updateUserFromDTO(existingUser, requestDTO);
        User updatedUser = userRepository.save(existingUser);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Delete user by ID (soft delete by setting isActive to false)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Hard delete user (permanent deletion)
     */
    public void permanentlyDeleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active users
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getActiveUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get users by age range
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get users with active subscriptions
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersWithActiveSubscriptions() {
        return userRepository.findUsersWithActiveSubscriptions()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Advanced search with multiple criteria
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String firstName, String lastName, String email,
                                             Boolean isActive, Integer minAge, Integer maxAge,
                                             Pageable pageable) {
        return userRepository.findUsersWithCriteria(firstName, lastName, email, isActive, minAge, maxAge, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToResponseDTO(user);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Get active user count
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Convert CreateUserRequestDTO to User entity
     */
    private User convertToEntity(CreateUserRequestDTO requestDTO) {
        User user = new User();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEmail(requestDTO.getEmail());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setAge(requestDTO.getAge());
        user.setIsActive(true);
        return user;
    }

    /**
     * Update User entity from UpdateUserRequestDTO
     */
    private void updateUserFromDTO(User user, UpdateUserRequestDTO requestDTO) {
        if (requestDTO.getFirstName() != null) {
            user.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getLastName() != null) {
            user.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getEmail() != null) {
            user.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(requestDTO.getPhoneNumber());
        }
        if (requestDTO.getAge() != null) {
            user.setAge(requestDTO.getAge());
        }
        if (requestDTO.getIsActive() != null) {
            user.setIsActive(requestDTO.getIsActive());
        }
    }

    /**
     * Convert User entity to UserResponseDTO
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhoneNumber(user.getPhoneNumber());
        responseDTO.setAge(user.getAge());
        responseDTO.setIsActive(user.getIsActive());
        responseDTO.setCreatedAt(user.getCreatedAt());
        responseDTO.setUpdatedAt(user.getUpdatedAt());
        responseDTO.setFullName(user.getFullName());
        responseDTO.setSubscriptionCount(user.getSubscriptions() != null ? user.getSubscriptions().size() : 0);

        // Set active subscriptions summary (only if subscriptions are loaded)
        if (user.getSubscriptions() != null && !user.getSubscriptions().isEmpty()) {
            List<UserResponseDTO.UserSubscriptionSummaryDTO> activeSubscriptions = user.getSubscriptions()
                    .stream()
                    .filter(sub -> "ACTIVE".equals(sub.getStatus().name()) && !sub.isExpired())
                    .map(sub -> new UserResponseDTO.UserSubscriptionSummaryDTO(
                            sub.getId(),
                            sub.getPlanName(),
                            sub.getStatus().name(),
                            sub.getEndDate().toString()))
                    .collect(Collectors.toList());
            responseDTO.setActiveSubscriptions(activeSubscriptions);
        }

        return responseDTO;
    }
}