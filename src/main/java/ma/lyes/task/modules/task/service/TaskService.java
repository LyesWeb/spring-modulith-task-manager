package ma.lyes.task.modules.task.service;

import ma.lyes.task.common.exception.ResourceNotFoundException;
import ma.lyes.task.modules.task.dto.TaskCreateDto;
import ma.lyes.task.modules.task.dto.TaskDto;
import ma.lyes.task.modules.task.dto.TaskUpdateDto;
import ma.lyes.task.modules.task.event.TaskAssignedEvent;
import ma.lyes.task.modules.task.event.TaskCreatedEvent;
import ma.lyes.task.modules.task.event.TaskStatusChangedEvent;
import ma.lyes.task.modules.task.model.Task;
import ma.lyes.task.modules.task.repository.TaskRepository;
import ma.lyes.task.modules.task.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TaskDto createTask(TaskCreateDto taskCreateDto) {
        Task task = Task.builder()
                .title(taskCreateDto.getTitle())
                .description(taskCreateDto.getDescription())
                .status(TaskStatus.TODO)
                .priority(taskCreateDto.getPriority())
                .dueDate(taskCreateDto.getDueDate())
                .projectId(taskCreateDto.getProjectId())
                .reporterId(taskCreateDto.getReporterId())
                .assigneeId(taskCreateDto.getAssigneeId())
                .estimatedHours(taskCreateDto.getEstimatedHours())
                .build();

        Task savedTask = taskRepository.save(task);

        // Publish task created event
        eventPublisher.publishEvent(new TaskCreatedEvent(savedTask.getId(), savedTask.getTitle(),
                savedTask.getProjectId(), savedTask.getReporterId()));

        // If task has an assignee, publish assignment event
        if (savedTask.getAssigneeId() != null) {
            eventPublisher.publishEvent(new TaskAssignedEvent(savedTask.getId(), savedTask.getTitle(),
                    savedTask.getProjectId(), savedTask.getAssigneeId(), savedTask.getReporterId()));
        }

        return mapToDto(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID taskId) {
        Task task = findTaskById(taskId);
        return mapToDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProject(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByProject(UUID projectId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByProjectId(projectId, pageable);
        return taskPage.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByAssignee(UUID assigneeId) {
        List<Task> tasks = taskRepository.findByAssigneeId(assigneeId);
        return tasks.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByAssignee(UUID assigneeId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByAssigneeId(assigneeId, pageable);
        return taskPage.map(this::mapToDto);
    }

    @Transactional
    public TaskDto updateTask(UUID taskId, TaskUpdateDto taskUpdateDto) {
        Task task = findTaskById(taskId);

        TaskStatus oldStatus = task.getStatus();
        UUID oldAssigneeId = task.getAssigneeId();

        // Update task fields if provided
        if (taskUpdateDto.getTitle() != null) {
            task.setTitle(taskUpdateDto.getTitle());
        }
        if (taskUpdateDto.getDescription() != null) {
            task.setDescription(taskUpdateDto.getDescription());
        }
        if (taskUpdateDto.getStatus() != null) {
            task.setStatus(taskUpdateDto.getStatus());
        }
        if (taskUpdateDto.getPriority() != null) {
            task.setPriority(taskUpdateDto.getPriority());
        }
        if (taskUpdateDto.getDueDate() != null) {
            task.setDueDate(taskUpdateDto.getDueDate());
        }
        if (taskUpdateDto.getAssigneeId() != null) {
            task.setAssigneeId(taskUpdateDto.getAssigneeId());
        }
        if (taskUpdateDto.getEstimatedHours() != null) {
            task.setEstimatedHours(taskUpdateDto.getEstimatedHours());
        }
        if (taskUpdateDto.getLoggedHours() != null) {
            task.setLoggedHours(taskUpdateDto.getLoggedHours());
        }

        Task updatedTask = taskRepository.save(task);

        // Check if status changed
        if (oldStatus != updatedTask.getStatus()) {
            eventPublisher.publishEvent(new TaskStatusChangedEvent(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getProjectId(),
                    oldStatus,
                    updatedTask.getStatus(),
                    updatedTask.getAssigneeId()));
        }

        // Check if assignee changed
        if (oldAssigneeId == null && updatedTask.getAssigneeId() != null ||
                (oldAssigneeId != null && !oldAssigneeId.equals(updatedTask.getAssigneeId()))) {
            eventPublisher.publishEvent(new TaskAssignedEvent(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getProjectId(),
                    updatedTask.getAssigneeId(),
                    updatedTask.getReporterId()));
        }

        return mapToDto(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        Task task = findTaskById(taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public TaskDto updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = findTaskById(taskId);
        TaskStatus oldStatus = task.getStatus();

        if (oldStatus != status) {
            task.setStatus(status);
            Task updatedTask = taskRepository.save(task);

            eventPublisher.publishEvent(new TaskStatusChangedEvent(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getProjectId(),
                    oldStatus,
                    status,
                    updatedTask.getAssigneeId()));

            return mapToDto(updatedTask);
        }

        return mapToDto(task);
    }

    @Transactional
    public TaskDto assignTask(UUID taskId, UUID assigneeId) {
        Task task = findTaskById(taskId);
        UUID oldAssigneeId = task.getAssigneeId();

        if (oldAssigneeId == null || !oldAssigneeId.equals(assigneeId)) {
            task.setAssigneeId(assigneeId);
            Task updatedTask = taskRepository.save(task);

            eventPublisher.publishEvent(new TaskAssignedEvent(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getProjectId(),
                    assigneeId,
                    updatedTask.getReporterId()));

            return mapToDto(updatedTask);
        }

        return mapToDto(task);
    }

    @Transactional
    public TaskDto logTime(UUID taskId, Integer hours) {
        Task task = findTaskById(taskId);
        Integer currentHours = task.getLoggedHours() != null ? task.getLoggedHours() : 0;
        task.setLoggedHours(currentHours + hours);
        return mapToDto(taskRepository.save(task));
    }

    private Task findTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private TaskDto mapToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .projectId(task.getProjectId())
                .assigneeId(task.getAssigneeId())
                .reporterId(task.getReporterId())
                .estimatedHours(task.getEstimatedHours())
                .loggedHours(task.getLoggedHours())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}