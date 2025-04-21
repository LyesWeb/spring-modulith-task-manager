package ma.lyes.task.modules.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private UUID id;
    private String name;
    private String description;
    private String status;
    private Set<UUID> memberIds;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
