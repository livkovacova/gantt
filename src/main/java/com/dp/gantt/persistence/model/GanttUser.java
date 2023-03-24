package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "gantt_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@AllArgsConstructor
@NoArgsConstructor
public class GanttUser {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 20)
        private String username;

        @NotBlank
        @Size(max = 120)
        private String password;

        @NotBlank
        @Size(max = 50)
        @Email
        private String email;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(	name = "gantt_user_roles",
                joinColumns = @JoinColumn(name = "gantt_user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
        private Set<Role> roles = new HashSet<>();

        public GanttUser(String username, String email, String password) {
                this.username = username;
                this.email = email;
                this.password = password;
        }

}
