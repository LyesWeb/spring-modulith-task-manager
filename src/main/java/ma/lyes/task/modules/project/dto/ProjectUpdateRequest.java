package ma.lyes.task.modules.project.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateRequest {
    private String name;
    private String description;

    @Pattern(regexp = "PLANNING|ACTIVE|ON_HOLD|COMPLETED|ARCHIVED",
            message = "Status must be one of: PLANNING, ACTIVE, ON_HOLD, COMPLETED, ARCHIVED")
    private String status;
}
