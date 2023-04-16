package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByName(String name);

    Optional<Task> findByWorkIdAndAndPhase_GanttChart_Id(Long workId, Long phaseId);
}
