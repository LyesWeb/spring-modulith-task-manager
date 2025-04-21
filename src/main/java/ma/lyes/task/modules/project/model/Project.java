package ma.lyes.task.modules.project.model;

import ma.lyes.task.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "user_id")
    @Builder.Default
    private Set<UUID> memberIds = new HashSet<>();

    // Enum for project status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    public enum ProjectStatus {
        PLANNING, ACTIVE, ON_HOLD, COMPLETED, ARCHIVED
    }
}