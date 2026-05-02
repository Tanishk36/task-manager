package com.taskmanager.config;

import com.taskmanager.entity.*;
import com.taskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Create Admin
        User admin = User.builder()
                .fullName("Admin User")
                .email("admin@taskmanager.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9876543210")
                .role(User.Role.ADMIN)
                .build();
        admin = userRepository.save(admin);

        // Create Members
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

        // Create Projects
        

Project p1 = Project.builder()
        .name("Website Redesign")
        .description("Redesign company website with modern UI")
        .createdBy(admin)
        .build();

// ✅ SET BEFORE SAVE
p1.setCreatedAt(java.time.LocalDateTime.now());

p1 = projectRepository.save(p1);


Project p2 = Project.builder()
        .name("Mobile App")
        .description("Build React Native mobile application")
        .createdBy(admin)
        .build();

// ✅ SET BEFORE SAVE
p2.setCreatedAt(java.time.LocalDateTime.now());

p2 = projectRepository.save(p2);

        // Create Tasks
      Task t1 = new Task();

t1.setTitle("Design wireframes");
t1.setDescription("Create low-fi wireframes for all pages");
t1.setDueDate(LocalDate.now().plusDays(7));
t1.setPriority(Task.Priority.HIGH);
t1.setStatus(Task.Status.IN_PROGRESS);
t1.setProject(p1);
t1.setAssignedTo(alice);
t1.setCreatedBy(admin);

// 🔥 IMPORTANT
t1.setCreatedAt(java.time.LocalDateTime.now());
t1.setUpdatedAt(java.time.LocalDateTime.now());

taskRepository.save(t1);

      Task t2 = new Task();

t2.setTitle("Design wireframes");
t2.setDescription("Create low-fi wireframes for all pages");
t2.setDueDate(LocalDate.now().plusDays(7));
t2.setPriority(Task.Priority.HIGH);
t2.setStatus(Task.Status.IN_PROGRESS);
t2.setProject(p1);
t2.setAssignedTo(alice);
t2.setCreatedBy(admin);

// 🔥 IMPORTANT
t2.setCreatedAt(java.time.LocalDateTime.now());
t2.setUpdatedAt(java.time.LocalDateTime.now());

taskRepository.save(t2);

      Task t3 = new Task();

t3.setTitle("Design wireframes");
t3.setDescription("Create low-fi wireframes for all pages");
t3.setDueDate(LocalDate.now().plusDays(7));
t3.setPriority(Task.Priority.HIGH);
t3.setStatus(Task.Status.IN_PROGRESS);
t3.setProject(p1);
t3.setAssignedTo(alice);
t3.setCreatedBy(admin);

// 🔥 IMPORTANT
t3.setCreatedAt(java.time.LocalDateTime.now());
t3.setUpdatedAt(java.time.LocalDateTime.now());

taskRepository.save(t3);


        Task t4 = new Task();

t4.setTitle("Design wireframes");
t4.setDescription("Create low-fi wireframes for all pages");
t4.setDueDate(LocalDate.now().plusDays(7));
t4.setPriority(Task.Priority.HIGH);
t4.setStatus(Task.Status.IN_PROGRESS);
t4.setProject(p1);
t4.setAssignedTo(alice);
t4.setCreatedBy(admin);

// 🔥 IMPORTANT
t4.setCreatedAt(java.time.LocalDateTime.now());
t4.setUpdatedAt(java.time.LocalDateTime.now());

taskRepository.save(t4);

       Task t5 = new Task();
t5.setTitle("Design wireframes");
t5.setDescription("Create low-fi wireframes for all pages");
t5.setDueDate(LocalDate.now().plusDays(7));
t5.setPriority(Task.Priority.HIGH);
t5.setStatus(Task.Status.IN_PROGRESS);
t5.setProject(p1);
t5.setAssignedTo(alice);
t5.setCreatedBy(admin);

// 🔥 IMPORTANT
t5.setCreatedAt(java.time.LocalDateTime.now());
t5.setUpdatedAt(java.time.LocalDateTime.now());

taskRepository.save(t5);



        System.out.println("✅ Seed data created successfully!");
        System.out.println("Admin: admin@taskmanager.com / admin123");
        System.out.println("Alice: alice@taskmanager.com / alice123");
        System.out.println("Bob: bob@taskmanager.com / bob123");
    }
}
