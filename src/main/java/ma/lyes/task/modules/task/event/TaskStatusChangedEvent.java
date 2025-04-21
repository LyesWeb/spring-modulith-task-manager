package ma.lyes.task.modules.task.event;


import ma.lyes.task.modules.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TaskStatusChangedEvent {
    private UUID taskId;
    private String taskTitle;
    private UUID projectId;
    private TaskStatus oldStatus;
    private TaskStatus newStatus;
    private UUID assigneeId;
}