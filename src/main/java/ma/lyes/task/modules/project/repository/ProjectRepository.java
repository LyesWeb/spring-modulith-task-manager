package ma.lyes.task.modules.project.repository;

import ma.lyes.task.modules.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE :userId = p.createdBy OR :userId MEMBER OF p.memberIds")
    List<Project> findAllByUserIsInvolved(@Param("userId") UUID userId);

    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' AND (:userId = p.createdBy OR :userId MEMBER OF p.memberIds)")
    List<Project> findActiveProjectsByUser(@Param("userId") UUID userId);
}