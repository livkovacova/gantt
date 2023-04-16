package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.GanttChart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GanttChartRepository extends JpaRepository<GanttChart, Long> {
}
