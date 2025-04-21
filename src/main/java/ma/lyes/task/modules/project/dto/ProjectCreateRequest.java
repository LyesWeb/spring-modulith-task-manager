package ma.lyes.task.modules.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateRequest {
    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @Pattern(regexp = "PLANNING|ACTIVE|ON_HOLD|COMPLETED|ARCHIVED",
            message = "Status must be one of: PLANNING, ACTIVE, ON_HOLD, COMPLETED, ARCHIVED")
    private String status = "ACTIVE";

    private Set<UUID> memberIds;
}