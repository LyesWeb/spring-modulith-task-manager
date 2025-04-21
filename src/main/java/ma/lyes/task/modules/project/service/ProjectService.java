package ma.lyes.task.modules.project.service;

import ma.lyes.task.common.exception.ResourceNotFoundException;
import ma.lyes.task.common.exception.UnauthorizedException;
import ma.lyes.task.modules.project.dto.ProjectCreateRequest;
import ma.lyes.task.modules.project.dto.ProjectDTO;
import ma.lyes.task.modules.project.dto.ProjectUpdateRequest;
import ma.lyes.task.modules.project.event.ProjectCreatedEvent;
import ma.lyes.task.modules.project.event.ProjectMemberAddedEvent;
import ma.lyes.task.modules.project.event.ProjectUpdatedEvent;
import ma.lyes.task.modules.project.model.Project;
import ma.lyes.task.modules.project.repository.ProjectRepository;
import ma.lyes.task.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public List<ProjectDTO> getAllProjects() {
        var currentUser = userService.getCurrentUserEntity();
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        List<Project> projects;
        if (isAdmin) {
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findAllByUserIsInvolved(currentUser.getId());
        }

        return projects.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO getProjectById(UUID id) {
        var project = findProjectById(id);
        checkUserAccess(project);
        return mapToDTO(project);
    }

    @Transactional
    public ProjectDTO createProject(ProjectCreateRequest request) {
        var currentUser = userService.getCurrentUserEntity();

        var project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(Project.ProjectStatus.valueOf(request.getStatus()))
                .build();

        // The creator is automatically a member
        project.getMemberIds().add(currentUser.getId());

        // Add other members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            project.getMemberIds().addAll(request.getMemberIds());
        }

        var savedProject = projectRepository.save(project);

        // Publish project created event
        eventPublisher.publishEvent(new ProjectCreatedEvent(
                savedProject.getId(),
                savedProject.getName(),
                currentUser.getId(),
                currentUser.getEmail())
        );

        return mapToDTO(savedProject);
    }

    @Transactional
    public ProjectDTO updateProject(UUID id, ProjectUpdateRequest request) {
        var project = findProjectById(id);
        checkProjectOwner(project);

        if (request.getName() != null) {
            project.setName(request.getName());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(request.getStatus()));
        }

        var updatedProject = projectRepository.save(project);

        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getStatus().toString())
        );

        return mapToDTO(updatedProject);
    }

    @Transactional
    public ProjectDTO addMemberToProject(UUID projectId, UUID userId) {
        var project = findProjectById(projectId);
        checkProjectOwner(project);

        if (project.getMemberIds().add(userId)) {
            var savedProject = projectRepository.save(project);

            // Publish member added event
            eventPublisher.publishEvent(new ProjectMemberAddedEvent(
                    savedProject.getId(),
                    savedProject.getName(),
                    userId)
            );

            return mapToDTO(savedProject);
        }

        return mapToDTO(project);
    }

    @Transactional
    public ProjectDTO removeMemberFromProject(UUID projectId, UUID userId) {
        var project = findProjectById(projectId);
        checkProjectOwner(project);

        // Cannot remove the project owner
        if (project.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Cannot remove the project owner");
        }

        project.getMemberIds().remove(userId);
        var savedProject = projectRepository.save(project);

        return mapToDTO(savedProject);
    }

    private Project findProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", "id", id));
    }

    private void checkUserAccess(Project project) {
        var currentUser = userService.getCurrentUserEntity();

        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        boolean isMember = project.getMemberIds().contains(currentUser.getId());
        boolean isOwner = project.getCreatedBy().equals(currentUser.getId());

        if (!isAdmin && !isMember && !isOwner) {
            throw new UnauthorizedException("You don't have access to this project");
        }
    }

    private void checkProjectOwner(Project project) {
        var currentUser = userService.getCurrentUserEntity();

        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        boolean isOwner = project.getCreatedBy().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Only the project owner or admins can modify the project");
        }
    }

    private ProjectDTO mapToDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().toString())
                .memberIds(project.getMemberIds())
                .createdAt(project.getCreatedAt())
                .createdBy(project.getCreatedBy())
                .build();
    }
}