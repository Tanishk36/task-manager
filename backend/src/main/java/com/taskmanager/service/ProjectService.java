package com.taskmanager.service;

import com.taskmanager.dto.DTOs.ProjectDto;
import com.taskmanager.dto.DTOs.ProjectRequest;
import com.taskmanager.dto.DTOs.UserDto;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= CREATE =================
    public ProjectDto createProject(ProjectRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .build();

        Project savedProject = projectRepository.save(project);

        return toDto(savedProject);
    }

    // ================= GET ALL =================
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ================= GET ONE =================
    public ProjectDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return toDto(project);
    }

    // ================= UPDATE =================
    public ProjectDto updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);

        return toDto(updatedProject);
    }

    // ================= DELETE =================
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    // ================= DTO MAPPING =================
    private ProjectDto toDto(Project project) {

        // 🔥 NEVER TOUCH project.getTasks() here
        // This avoids lazy loading crash

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .createdBy(toUserDto(project.getCreatedBy()))
                .taskCount(0) // safe placeholder
                .build();
    }

    private UserDto toUserDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
}