package org.tix.backend.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Getter
@Setter
public class History {
    @Id
    private Long id;
    // need to refactor
    @ElementCollection
    private ArrayList<Long> itemIds;
    @ElementCollection
    private HashMap<Long, String> itemNames;

}
