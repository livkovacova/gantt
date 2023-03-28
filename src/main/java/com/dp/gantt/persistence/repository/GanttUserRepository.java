package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.GanttUser;
import com.dp.gantt.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GanttUserRepository extends JpaRepository<GanttUser, Long> {
    Optional<GanttUser> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<GanttUser> findAllByRolesContains(Role role);
}
