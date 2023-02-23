package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
