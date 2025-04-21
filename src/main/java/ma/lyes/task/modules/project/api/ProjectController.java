package ma.lyes.task.modules.project.api;

import ma.lyes.task.modules.project.dto.ProjectCreateRequest;
import ma.lyes.task.modules.project.dto.ProjectDTO;
import ma.lyes.task.modules.project.dto.ProjectUpdateRequest;
import ma.lyes.task.modules.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management API")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get all projects the user has access to")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PostMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Add a member to a project")
    public ResponseEntity<ProjectDTO> addMemberToProject(
            @PathVariable UUID projectId,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(projectService.addMemberToProject(projectId, userId));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Remove a member from a project")
    public ResponseEntity<ProjectDTO> removeMemberFromProject(
            @PathVariable UUID projectId,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(projectService.removeMemberFromProject(projectId, userId));
    }
}