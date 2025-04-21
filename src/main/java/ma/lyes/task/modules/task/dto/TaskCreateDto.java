package ma.lyes.task.modules.task.dto;

import ma.lyes.task.modules.task.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private UUID assigneeId;

    @NotNull(message = "Reporter ID is required")
    private UUID reporterId;

    private Integer estimatedHours;
}