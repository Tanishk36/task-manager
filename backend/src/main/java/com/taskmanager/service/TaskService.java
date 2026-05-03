package com.taskmanager.service;

import com.taskmanager.dto.DTOs.DashboardStats;
import com.taskmanager.dto.DTOs.ProjectDto;
import com.taskmanager.dto.DTOs.TaskDto;
import com.taskmanager.dto.DTOs.TaskRequest;
import com.taskmanager.dto.DTOs.UserDto;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ IMPORT

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // ✅ FIX: keeps Hibernate session open for all methods
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    public TaskDto createTask(TaskRequest request, String creatorEmail) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User assignee = null;
        if (request.getAssignedToId() != null) {
            assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(Task.Status.PENDING);
        task.setProject(project);
        task.setAssignedTo(assignee);
        task.setCreatedBy(creator);

        return toDto(taskRepository.save(task));
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByAssignedToId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto updateStatus(Long taskId, Task.Status status, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == User.Role.MEMBER
                && (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(user.getId()))) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        task.setStatus(status);
        return toDto(taskRepository.save(task));
    }

    public TaskDto updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) task.setPriority(request.getPriority());

        if (request.getAssignedToId() != null) {
            User assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            task.setAssignedTo(assignee);
        }

        return toDto(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public DashboardStats getDashboardStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == User.Role.ADMIN) {
            return DashboardStats.builder()
                    .totalTasks(taskRepository.count())
                    .pendingTasks(taskRepository.countByStatus(Task.Status.PENDING))
                    .inProgressTasks(taskRepository.countByStatus(Task.Status.IN_PROGRESS))
                    .completedTasks(taskRepository.countByStatus(Task.Status.COMPLETED))
                    .overdueTasks(taskRepository.findOverdueTasks(LocalDate.now()).size())
                    .totalProjects(projectRepository.count())
                    .totalUsers(userRepository.count())
                    .build();
        }

        return DashboardStats.builder()
                .totalTasks(taskRepository.countByAssignedToId(user.getId()))
                .pendingTasks(taskRepository.countByAssignedToIdAndStatus(user.getId(), Task.Status.PENDING))
                .inProgressTasks(taskRepository.countByAssignedToIdAndStatus(user.getId(), Task.Status.IN_PROGRESS))
                .completedTasks(taskRepository.countByAssignedToIdAndStatus(user.getId(), Task.Status.COMPLETED))
                .overdueTasks(taskRepository.findOverdueTasksByUser(user.getId(), LocalDate.now()).size())
                .totalProjects(0)
                .totalUsers(0)
                .build();
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .overdue(task.getDueDate().isBefore(LocalDate.now())
                        && task.getStatus() != Task.Status.COMPLETED)
                .project(task.getProject() != null ? ProjectDto.builder()
                        .id(task.getProject().getId())
                        .name(task.getProject().getName())
                        .build() : null)
                .assignedTo(task.getAssignedTo() != null ? UserDto.builder()
                        .id(task.getAssignedTo().getId())
                        .fullName(task.getAssignedTo().getFullName())
                        .email(task.getAssignedTo().getEmail())
                        .build() : null)
                .createdBy(task.getCreatedBy() != null ? UserDto.builder()
                        .id(task.getCreatedBy().getId())
                        .fullName(task.getCreatedBy().getFullName())
                        .email(task.getCreatedBy().getEmail())
                        .build() : null)
                .build();
    }
}