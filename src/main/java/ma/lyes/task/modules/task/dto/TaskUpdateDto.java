package ma.lyes.task.modules.task.dto;

import ma.lyes.task.modules.task.enums.TaskPriority;
import ma.lyes.task.modules.task.enums.TaskStatus;
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
public class TaskUpdateDto {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private UUID assigneeId;
    private Integer estimatedHours;
    private Integer loggedHours;
}
