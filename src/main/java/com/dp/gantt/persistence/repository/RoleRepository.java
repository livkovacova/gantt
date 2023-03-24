package com.dp.gantt.persistence.repository;

import com.dp.gantt.persistence.model.Role;
import com.dp.gantt.persistence.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
