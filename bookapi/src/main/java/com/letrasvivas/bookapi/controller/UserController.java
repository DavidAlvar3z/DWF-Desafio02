package com.letrasvivas.bookapi.controller;

import com.letrasvivas.bookapi.dto.request.CreateUserRequestDTO;
import com.letrasvivas.bookapi.dto.request.UpdateUserRequestDTO;
import com.letrasvivas.bookapi.dto.response.UserResponseDTO;
import com.letrasvivas.bookapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users in the system")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users with pagination",
            description = "Retrieve a paginated list of all users in the system. Supports sorting by multiple fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort by field(s). Use format: field,direction",
                    example = "firstName,asc")
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Create a new user",
            description = "Create a new user in the system with the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "User with this email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "User data to create", required = true)
            @Valid @RequestBody CreateUserRequestDTO createUserDTO
    ) {
        UserResponseDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(
            summary = "Update an existing user",
            description = "Update user information. Only provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UpdateUserRequestDTO updateUserDTO
    ) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Soft delete a user",
            description = "Deactivate a user by setting isActive to false. User data is preserved."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        response.put("userId", id.toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Permanently delete a user",
            description = "Permanently remove a user from the system. This action cannot be undone."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User permanently deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Map<String, String>> permanentlyDeleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        userService.permanentlyDeleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User permanently deleted");
        response.put("userId", id.toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search users by name",
            description = "Search users by first name or last name (case-insensitive partial match)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByName(
            @Parameter(description = "Name to search for", required = true, example = "John")
            @RequestParam String name
    ) {
        List<UserResponseDTO> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get active users",
            description = "Retrieve all users that are currently active in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active users retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        List<UserResponseDTO> activeUsers = userService.getActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }

    @Operation(
            summary = "Get users by age range",
            description = "Retrieve users within a specific age range"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid age parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/age-range")
    public ResponseEntity<List<UserResponseDTO>> getUsersByAgeRange(
            @Parameter(description = "Minimum age", required = true, example = "18")
            @RequestParam @Min(16) @Max(120) Integer minAge,

            @Parameter(description = "Maximum age", required = true, example = "65")
            @RequestParam @Min(16) @Max(120) Integer maxAge
    ) {
        List<UserResponseDTO> users = userService.getUsersByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get users with active subscriptions",
            description = "Retrieve all users who currently have at least one active subscription"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users with active subscriptions retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/with-subscriptions")
    public ResponseEntity<List<UserResponseDTO>> getUsersWithActiveSubscriptions() {
        List<UserResponseDTO> users = userService.getUsersWithActiveSubscriptions();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Advanced user search",
            description = "Search users using multiple criteria with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<UserResponseDTO>> advancedSearch(
            @Parameter(description = "First name filter", example = "John")
            @RequestParam(required = false) String firstName,

            @Parameter(description = "Last name filter", example = "Doe")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Email filter", example = "john@example.com")
            @RequestParam(required = false) String email,

            @Parameter(description = "Active status filter", example = "true")
            @RequestParam(required = false) Boolean isActive,

            @Parameter(description = "Minimum age", example = "18")
            @RequestParam(required = false) @Min(16) Integer minAge,

            @Parameter(description = "Maximum age", example = "65")
            @RequestParam(required = false) @Max(120) Integer maxAge,

            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort criteria", example = "firstName,asc")
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDTO> users = userService.searchUsers(
                firstName, lastName, email, isActive, minAge, maxAge, pageable
        );
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user by email",
            description = "Retrieve a user by their email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "User email", required = true, example = "john@example.com")
            @PathVariable String email
    ) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Check if email exists",
            description = "Check whether an email address is already registered in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email check completed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"exists\": true, \"email\": \"john@example.com\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Map<String, Object>> checkEmailExists(
            @Parameter(description = "Email to check", required = true, example = "john@example.com")
            @PathVariable String email
    ) {
        boolean exists = userService.emailExists(email);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("email", email);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user statistics",
            description = "Get general statistics about users in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"totalUsers\": 100, \"activeUsers\": 85}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("activeUsers", userService.getActiveUserCount());
        return ResponseEntity.ok(stats);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Pageable createPageable(int page, int size, String[] sort) {
        Sort.Order[] orders = new Sort.Order[sort.length];
        for (int i = 0; i < sort.length; i++) {
            String[] sortParams = sort[i].split(",");
            String field = sortParams[0];
            String direction = sortParams.length > 1 ? sortParams[1] : "asc";
            orders[i] = new Sort.Order(Sort.Direction.fromString(direction), field);
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}