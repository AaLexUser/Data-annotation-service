package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
}
