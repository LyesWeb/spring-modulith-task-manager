package ma.lyes.task.modules.project.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreatedEvent {
    private UUID projectId;
    private String projectName;
    private UUID createdBy;
    private String creatorEmail;
}