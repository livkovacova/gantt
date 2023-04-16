package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, Long> {

    List<Phase> findAllByGanttChart_Id(Long id);
}
