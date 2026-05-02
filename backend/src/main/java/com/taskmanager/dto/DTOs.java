package com.taskmanager.dto;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DTOs {

    // ========== AUTH DTOs ==========
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        @NotBlank(message = "Full name is required")
        @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "Name must contain only letters and spaces (2-50 characters)")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone must be 10-15 digits, optionally starting with +")
        private String phone;

        private User.Role role = User.Role.MEMBER;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String fullName;
        private String email;
        private String role;
    }

    // ========== USER DTOs ==========
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserDto {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
        private String role;
    }

    // ========== PROJECT DTOs ==========
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProjectRequest {
        @NotBlank(message = "Project name is required")
        @Size(min = 3, max = 100, message = "Project name must be 3-100 characters")
        private String name;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProjectDto {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private UserDto createdBy;
        private int taskCount;
    }

    // ========== TASK DTOs ==========
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TaskRequest {
        @NotBlank(message = "Task title is required")
        @Size(min = 3, max = 200, message = "Title must be 3-200 characters")
        private String title;

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        private String description;

        @NotNull(message = "Due date is required")
        private LocalDate dueDate;

        private Task.Priority priority = Task.Priority.MEDIUM;

        @NotNull(message = "Project ID is required")
        private Long projectId;

        private Long assignedToId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private Task.Status status;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TaskDto {
        private Long id;
        private String title;
        private String description;
        private String status;
        private String priority;
        private LocalDate dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private ProjectDto project;
        private UserDto assignedTo;
        private UserDto createdBy;
        private boolean overdue;
    }

    // ========== DASHBOARD DTOs ==========
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DashboardStats {
        private long totalTasks;
        private long pendingTasks;
        private long inProgressTasks;
        private long completedTasks;
        private long overdueTasks;
        private long totalProjects;
        private long totalUsers;
    }
}
