package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByManager_id(Long manager_id);

    List<Project> findAllByMembers_id(Long members_id);

    Page<Project> findAllByManager_id(Long manager_id, Pageable pageable);

    Page<Project> findAllByMembers_id(Long members_id, Pageable pageable);
}
