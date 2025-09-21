package com.letrasvivas.bookapi;

import com.letrasvivas.bookapi.dto.request.CreateUserRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateUserRequestDTO;
import com.letrasvivas.bookapi.dto.response.UserResponseDTO;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.exception.DuplicateResourceException;
import com.letrasvivas.bookapi.exception.ResourceNotFoundException;
import com.letrasvivas.bookapi.repository.UserRepository;
import com.letrasvivas.bookapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequestDTO createUserRequestDTO;
    private UpdateUserRequestDTO updateUserRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhoneNumber("+1234567890");
        testUser.setAge(25);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        createUserRequestDTO = new CreateUserRequestDTO();
        createUserRequestDTO.setFirstName("Jane");
        createUserRequestDTO.setLastName("Smith");
        createUserRequestDTO.setEmail("jane.smith@example.com");
        createUserRequestDTO.setPhoneNumber("+1987654321");
        createUserRequestDTO.setAge(30);

        updateUserRequestDTO = new UpdateUserRequestDTO();
        updateUserRequestDTO.setFirstName("John Updated");
        updateUserRequestDTO.setAge(26);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        verify(userRepository).findById(999L);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Given
        when(userRepository.existsByEmail(createUserRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.createUser(createUserRequestDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).existsByEmail(createUserRequestDTO.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowDuplicateResourceException() {
        // Given
        when(userRepository.existsByEmail(createUserRequestDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(createUserRequestDTO);
        });
        verify(userRepository).existsByEmail(createUserRequestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.updateUser(1L, updateUserRequestDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(999L, updateUserRequestDTO);
        });
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldSoftDeleteUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        assertFalse(testUser.getIsActive());
    }

    @Test
    void searchUsersByName_ShouldReturnMatchingUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.searchUsersByName("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(userRepository).findByNameContainingIgnoreCase("John");
    }

    @Test
    void getActiveUsers_ShouldReturnOnlyActiveUsers() {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByIsActiveTrue()).thenReturn(activeUsers);

        // When
        List<UserResponseDTO> result = userService.getActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(userRepository).findByIsActiveTrue();
    }

    @Test
    void emailExists_WithExistingEmail_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When
        boolean result = userService.emailExists("john.doe@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("john.doe@example.com");
    }

    @Test
    void emailExists_WithNonExistingEmail_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = userService.emailExists("nonexistent@example.com");

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
}