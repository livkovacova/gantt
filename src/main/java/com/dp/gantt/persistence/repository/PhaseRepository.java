package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseRepository extends JpaRepository<Phase, Long> {

}
