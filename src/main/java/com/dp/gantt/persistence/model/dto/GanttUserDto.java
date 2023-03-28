package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Role;
import com.dp.gantt.persistence.model.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanttUserDto {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles = new HashSet<>();
}
