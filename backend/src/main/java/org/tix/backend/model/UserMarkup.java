package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
public class UserMarkup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Task task; // К какой задаче относится разметка

    @ManyToOne
    private User assessor; // Кто выполнил разметку

    @ElementCollection
    @CollectionTable(name = "user_markup_data", joinColumns = @JoinColumn(name = "user_markup_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "selected_value", columnDefinition = "TEXT")
    private Map<String, String> selections;
}
