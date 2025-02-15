package org.tix.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Getter
@Setter
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // need to refactor
    @ElementCollection
    private ArrayList<Long> itemIds;
    @ElementCollection
    private HashMap<Long, String> itemNames;

}
