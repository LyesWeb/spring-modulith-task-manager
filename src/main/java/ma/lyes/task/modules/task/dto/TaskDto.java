package ma.lyes.task.modules.task.dto;

import ma.lyes.task.modules.task.enums.TaskPriority;
import ma.lyes.task.modules.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private UUID projectId;
    private UUID assigneeId;
    private UUID reporterId;
    private Integer estimatedHours;
    private Integer loggedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
