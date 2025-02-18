package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import java.util.Map;

@Entity
@Data
@Table(name = "honeypot_tasks")
public class HoneypotTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "correct_answers", columnDefinition = "jsonb")
    private Map<String, String> correctAnswers;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
} 