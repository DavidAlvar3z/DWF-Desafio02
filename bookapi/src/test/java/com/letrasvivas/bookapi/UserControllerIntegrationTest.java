package com.letrasvivas.bookapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letrasvivas.bookapi.dto.request.CreateUserRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateUserRequestDTO;
import com.letrasvivas.bookapi.entity.User;
import com.letrasvivas.bookapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString; // ← Importación específica agregada
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfully() throws Exception {
        CreateUserRequestDTO createUserDTO = new CreateUserRequestDTO(
                "John", "Doe", "john.doe@example.com", "+1234567890", 25
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+1234567890"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return validation errors for invalid user data")
    void shouldReturnValidationErrorsForInvalidData() throws Exception {
        CreateUserRequestDTO invalidUser = new CreateUserRequestDTO(
                "", "D", "invalid-email", "123", 15
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists())
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.lastName").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.phoneNumber").exists())
                .andExpect(jsonPath("$.validationErrors.age").exists());
    }

    @Test
    @DisplayName("Should return conflict error when creating user with existing email")
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        // Create first user
        User existingUser = new User("Jane", "Smith", "jane@example.com", "+1111111111", 28);
        userRepository.save(existingUser);

        // Try to create user with same email
        CreateUserRequestDTO duplicateUser = new CreateUserRequestDTO(
                "John", "Doe", "jane@example.com", "+2222222222", 25
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Resource Already Exists"))
                .andExpect(jsonPath("$.message").value(containsString("already exists"))); // ← Ahora debería funcionar
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        User user = new User("Alice", "Johnson", "alice@example.com", "+3333333333", 32);
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.fullName").value("Alice Johnson"));
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("not found"))); // ← Ahora debería funcionar
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        User user = new User("Bob", "Wilson", "bob@example.com", "+4444444444", 29);
        User savedUser = userRepository.save(user);

        UpdateUserRequestDTO updateDTO = new UpdateUserRequestDTO();
        updateDTO.setFirstName("Robert");
        updateDTO.setAge(30);

        mockMvc.perform(put("/api/v1/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Robert"))
                .andExpect(jsonPath("$.lastName").value("Wilson"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.fullName").value("Robert Wilson"));
    }

    @Test
    @DisplayName("Should get all users with pagination")
    void shouldGetAllUsersWithPagination() throws Exception {
        // Create multiple users
        for (int i = 1; i <= 15; i++) {
            User user = new User("User" + i, "Test", "user" + i + "@example.com", "+555000000" + i, 25 + i);
            userRepository.save(user);
        }

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() throws Exception {
        User user1 = new User("John", "Smith", "john.smith@example.com", "+6666666666", 30);
        User user2 = new User("Jane", "Johnson", "jane.johnson@example.com", "+7777777777", 28);
        User user3 = new User("Bob", "Brown", "bob.brown@example.com", "+8888888888", 35);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        mockMvc.perform(get("/api/v1/users/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].firstName", hasItem("John")));
    }

    @Test
    @DisplayName("Should get users by age range")
    void shouldGetUsersByAgeRange() throws Exception {
        User youngUser = new User("Young", "User", "young@example.com", "+1111111111", 20);
        User middleUser = new User("Middle", "User", "middle@example.com", "+2222222222", 35);
        User oldUser = new User("Old", "User", "old@example.com", "+3333333333", 55);

        userRepository.save(youngUser);
        userRepository.save(middleUser);
        userRepository.save(oldUser);

        mockMvc.perform(get("/api/v1/users/age-range")
                        .param("minAge", "30")
                        .param("maxAge", "40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Middle"));
    }

    @Test
    @DisplayName("Should soft delete user successfully")
    void shouldSoftDeleteUserSuccessfully() throws Exception {
        User user = new User("Delete", "Test", "delete@example.com", "+9999999999", 25);
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deactivated successfully"))
                .andExpect(jsonPath("$.userId").value(savedUser.getId().toString()));

        // Verify user is deactivated
        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @DisplayName("Should get user statistics")
    void shouldGetUserStatistics() throws Exception {
        User activeUser = new User("Active", "User", "active@example.com", "+1111111111", 25);
        User inactiveUser = new User("Inactive", "User", "inactive@example.com", "+2222222222", 30);
        inactiveUser.setIsActive(false);

        userRepository.save(activeUser);
        userRepository.save(inactiveUser);

        mockMvc.perform(get("/api/v1/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.activeUsers").value(1));
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() throws Exception {
        CreateUserRequestDTO invalidEmailUser = new CreateUserRequestDTO(
                "Test", "User", "invalid-email-format", "+1234567890", 25
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("Should validate phone number format")
    void shouldValidatePhoneNumberFormat() throws Exception {
        CreateUserRequestDTO invalidPhoneUser = new CreateUserRequestDTO(
                "Test", "User", "test@example.com", "invalid-phone", 25
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPhoneUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.phoneNumber").exists());
    }
}