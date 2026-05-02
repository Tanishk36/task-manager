package com.taskmanager.controller;

import com.taskmanager.dto.DTOs.*;
import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskRequest request,
                                               Authentication auth) {
        return ResponseEntity.ok(taskService.createTask(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getMyTasks(Authentication auth) {
        return ResponseEntity.ok(taskService.getTasksByUser(auth.getName()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable Long id,
                                                 @Valid @RequestBody StatusUpdateRequest request,
                                                 Authentication auth) {
        return ResponseEntity.ok(taskService.updateStatus(id, request.getStatus(), auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id,
                                               @RequestBody TaskRequest request,
                                               Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboard(Authentication auth) {
        return ResponseEntity.ok(taskService.getDashboardStats(auth.getName()));
    }
}
