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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    public ProjectDto createProject(ProjectRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .build();

        return toDto(projectRepository.save(project));
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProject(Long id) {
        return toDto(projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found")));
    }

    public ProjectDto updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return toDto(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .createdBy(toUserDto(project.getCreatedBy()))
                .taskCount(0)
                .build();
    }

    // ✅ Added missing helper — was referenced but never defined
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