package org.tix.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Assign {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batchId;
}
