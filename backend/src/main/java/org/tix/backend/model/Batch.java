package org.tix.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Batch {
    @Id
    private Long id;
    private String url;
    private Boolean isActive;
}
