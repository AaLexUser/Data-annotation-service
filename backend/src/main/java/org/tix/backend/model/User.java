package org.tix.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.tix.backend.util.Role;

@Entity
@Getter
@Setter
public class User {
    @Id
    private Long id;
    private String login;
    private String password;
    private Role role;

}
