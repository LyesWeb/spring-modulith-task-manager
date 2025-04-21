package ma.lyes.task.modules.task.model;

import ma.lyes.task.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import ma.lyes.task.modules.task.enums.TaskPriority;
import ma.lyes.task.modules.task.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    private UUID projectId;

    @Column
    private UUID assigneeId;

    @Column
    private UUID reporterId;

    @Column
    private LocalDate dueDate;

    @Column
    private Integer estimatedHours;

    @Column
    private Integer loggedHours = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;
}