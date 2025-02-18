package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Map;

@Entity
@Data
@Table(name = "task_answers")
public class TaskAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "assessor_id")
    private User assessor;

    @Column(name = "answers", columnDefinition = "jsonb")
    private Map<String, String> answers;

    @Column(name = "is_honeypot")
    private boolean isHoneypot;

    @Column(name = "correct_answers", columnDefinition = "jsonb")
    private Map<String, String> correctAnswers;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
} 