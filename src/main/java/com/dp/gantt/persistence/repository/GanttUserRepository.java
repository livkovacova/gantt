package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.GanttUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GanttUserRepository extends JpaRepository<GanttUser, Long> {
    Optional<GanttUser> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
