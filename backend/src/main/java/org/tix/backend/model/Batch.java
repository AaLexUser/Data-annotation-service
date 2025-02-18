package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String format;
    private LocalDateTime uploadedAt;
    private Boolean isActive;

    // Educational/Honeypot features
    private Boolean isEducational = false;

    @ElementCollection
    @CollectionTable(name = "batch_correct_answers", joinColumns = @JoinColumn(name = "batch_id"))
    @MapKeyColumn(name = "task_id")
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private Map<Long, String> correctAnswers = new HashMap<>();

    @ManyToMany
    @JoinTable(
            name = "batch_user",
            joinColumns = @JoinColumn(name = "batch_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> availableUsers;
}
