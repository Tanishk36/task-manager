package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByProjectId(Long projectId);
    List<Task> findByStatus(Task.Status status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.dueDate < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksByUser(Long userId, LocalDate today);

    long countByStatus(Task.Status status);
    long countByAssignedToId(Long userId);
    long countByAssignedToIdAndStatus(Long userId, Task.Status status);
}
