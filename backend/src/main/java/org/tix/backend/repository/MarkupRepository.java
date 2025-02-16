package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Markup;

import java.util.Optional;

@Repository
public interface MarkupRepository extends JpaRepository<Markup, Long> {

    Optional<Markup> findByBatchId(Batch batchId);
}
