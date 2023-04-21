package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByName(String name);

    Optional<Task> findByWorkIdAndAndPhase_GanttChart_Id(Long workId, Long phaseId);

    List<Task> findAllByPhase_IdAndPhase_GanttChart_Id(Long phaseId, Long ganttChartId);

    @Transactional
    @Query(value = "DELETE FROM task_assignees WHERE task_id = :taskId", nativeQuery = true)
    void deleteAssigneesForTask(@Param("taskId") Long taskId);

    @Transactional
    @Query(value = "DELETE FROM task_predecessors WHERE task_id = :taskId", nativeQuery = true)
    void deletePredecessorsForTask(@Param("taskId") Long taskId);

    @Query(value = "SELECT COUNT(id) FROM task WHERE phase_id = :phaseId", nativeQuery = true)
    Integer getSumByPhaseId(@Param("phaseId")Long phaseId);

}
