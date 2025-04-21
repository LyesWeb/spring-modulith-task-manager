package ma.lyes.task.modules.task.repository;

import ma.lyes.task.modules.task.enums.TaskStatus;
import ma.lyes.task.modules.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);

    Page<Task> findByProjectId(UUID projectId, Pageable pageable);

    List<Task> findByAssigneeId(UUID assigneeId);

    Page<Task> findByAssigneeId(UUID assigneeId, Pageable pageable);

    List<Task> findByAssigneeIdAndStatus(UUID assigneeId, TaskStatus status);

    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
}