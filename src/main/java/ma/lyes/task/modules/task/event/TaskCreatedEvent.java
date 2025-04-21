package ma.lyes.task.modules.task.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TaskCreatedEvent {
    private UUID taskId;
    private String taskTitle;
    private UUID projectId;
    private UUID reporterId;
}
