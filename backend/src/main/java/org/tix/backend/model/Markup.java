package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Entity
@Getter
@Setter
public class Markup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long version;

    @OneToOne
    private Batch batchId;

    @ElementCollection
    private HashMap<String, String> elements;


}
