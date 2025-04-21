package ma.lyes.task.modules.task.api;

import ma.lyes.task.modules.task.service.TaskService;
import ma.lyes.task.modules.task.dto.TaskCreateDto;
import ma.lyes.task.modules.task.dto.TaskDto;
import ma.lyes.task.modules.task.dto.TaskUpdateDto;
import ma.lyes.task.modules.task.enums.TaskStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskCreateDto) {
        return new ResponseEntity<>(taskService.createTask(taskCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @GetMapping("/project/{projectId}/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDto>> getTasksByProjectPaged(
            @PathVariable UUID projectId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, pageable));
    }

    @GetMapping("/assignee/{assigneeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(@PathVariable UUID assigneeId) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId));
    }

    @GetMapping("/assignee/{assigneeId}/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDto>> getTasksByAssigneePaged(
            @PathVariable UUID assigneeId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, pageable));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskUpdateDto));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurityService.isProjectMember(authentication.principal, #taskId)")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable UUID taskId,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @PatchMapping("/{taskId}/assign/{assigneeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable UUID taskId,
            @PathVariable UUID assigneeId) {
        return ResponseEntity.ok(taskService.assignTask(taskId, assigneeId));
    }

    @PostMapping("/{taskId}/log-time")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> logTime(
            @PathVariable UUID taskId,
            @RequestParam Integer hours) {
        return ResponseEntity.ok(taskService.logTime(taskId, hours));
    }
}