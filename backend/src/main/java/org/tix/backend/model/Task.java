package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn
    private Batch batchId;
    @ElementCollection
    @CollectionTable(name = "task_row_from_batch", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "row_from_batch_key")
    @Column(name = "row_from_batch", columnDefinition = "TEXT")
    private Map<String, String> rowFromBatch = new HashMap<>();

    private Integer finiteOverlaps;
    private Integer currentOverlaps;


}
