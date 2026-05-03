package com.taskmanager.config;

import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .fullName("Admin User")
                .email("admin@taskmanager.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9876543210")
                .role(User.Role.ADMIN)
                .build());

        User alice = userRepository.save(User.builder()
                .fullName("Alice Johnson")
                .email("alice@taskmanager.com")
                .password(passwordEncoder.encode("alice123"))
                .phone("9876543211")
                .role(User.Role.MEMBER)
                .build());

        User bob = userRepository.save(User.builder()
                .fullName("Bob Smith")
                .email("bob@taskmanager.com")
                .password(passwordEncoder.encode("bob123"))
                .phone("9876543212")
                .role(User.Role.MEMBER)
                .build());

        Project websiteProject = projectRepository.save(Project.builder()
                .name("Website Redesign")
                .description("Redesign company website with modern UI")
                .createdBy(admin)
                .build());

        Project mobileProject = projectRepository.save(Project.builder()
                .name("Mobile App")
                .description("Build React Native mobile application")
                .createdBy(admin)
                .build());

        createTask(
                "Design wireframes",
                "Create low-fi wireframes for all pages",
                LocalDate.now().plusDays(7),
                Task.Priority.HIGH,
                Task.Status.IN_PROGRESS,
                websiteProject,
                alice,
                admin
        );

        createTask(
                "Build login screen",
                "Implement login and signup pages with validation",
                LocalDate.now().plusDays(10),
                Task.Priority.HIGH,
                Task.Status.PENDING,
                websiteProject,
                bob,
                admin
        );

        createTask(
                "Create dashboard API",
                "Expose task and project statistics for the dashboard",
                LocalDate.now().plusDays(14),
                Task.Priority.MEDIUM,
                Task.Status.PENDING,
                mobileProject,
                alice,
                admin
        );

        logger.info("Seed data created successfully.");
        logger.info("Admin: admin@taskmanager.com / admin123");
        logger.info("Alice: alice@taskmanager.com / alice123");
        logger.info("Bob: bob@taskmanager.com / bob123");
    }

    private void createTask(String title,
                            String description,
                            LocalDate dueDate,
                            Task.Priority priority,
                            Task.Status status,
                            Project project,
                            User assignee,
                            User creator) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setStatus(status);
        task.setProject(project);
        task.setAssignedTo(assignee);
        task.setCreatedBy(creator);

        taskRepository.save(task);
    }
}
