package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Markup;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkupRepository extends JpaRepository<Markup, Long> {

    Optional<Markup> findByBatchId(Batch batchId);

    Optional<Markup> findByTaskIdAndAssessorId(Long taskId, Long assessorId);

    List<Markup> findByBatchIdOrderByVersionDesc(Batch batchId);
    
    Optional<Markup> findByBatchIdAndIsActiveTrue(Batch batchId);
    
    @Query("SELECT COALESCE(MAX(m.version), 0) FROM Markup m WHERE m.batchId = :batchId")
    Integer findMaxVersionByBatchId(Batch batchId);
    
    List<Markup> findByBatchIdAndVersion(Batch batchId, Integer version);
}
